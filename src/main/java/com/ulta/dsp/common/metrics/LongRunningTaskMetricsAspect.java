/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.metrics;

import static com.ulta.dsp.common.constants.UltaCommonConstants.FLAG_NAME;
import static com.ulta.dsp.common.constants.UltaCommonConstants.METRICS_CLASS;
import static com.ulta.dsp.common.constants.UltaCommonConstants.METRICS_LONG_RUNNING_THREAD_DURATION_SECONDS;
import static com.ulta.dsp.common.constants.UltaCommonConstants.METRICS_LONG_RUNNING_THREAD_EXCEED_TOTAL;
import static com.ulta.dsp.common.constants.UltaCommonConstants.METRICS_METHOD;

import com.ulta.dsp.common.annotation.MonitorLongRunning;
import com.ulta.dsp.common.annotation.Tag;
import com.ulta.dsp.common.config.LaunchDarklyConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LongRunningTaskMetricsAspect {

  private final MeterRegistry meterRegistry;
  private final ConfigurableBeanFactory beanFactory;
  @Autowired private LaunchDarklyConfig launchDarklyConfig;

  public LongRunningTaskMetricsAspect(MeterRegistry meterRegistry, ConfigurableBeanFactory beanFactory) {
    this.meterRegistry = meterRegistry;
      this.beanFactory = beanFactory;
  }

  @Around("@annotation(com.ulta.dsp.common.annotation.MonitorLongRunning)")
  public Object monitorDuration(ProceedingJoinPoint joinPoint) throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    MonitorLongRunning annotation = method.getAnnotation(MonitorLongRunning.class);

    Map<String, String> tagsMap =
        Arrays.stream(annotation.tags()).collect(Collectors.toMap(Tag::key, Tag::value));
    // find the LD flagName from tag
    String ldFlagName = tagsMap.get(FLAG_NAME);

    boolean ldFlagEnabled = false; // Default to false if no flag is specified
    if (ldFlagName != null) {
      ldFlagEnabled = launchDarklyConfig.flagEnabled(ldFlagName);
    }

    if (ldFlagEnabled) {
      // This timer will produce the 'custom_long_running_thread_duration_seconds_max' metric
      log.debug(
          "LongRunningTaskMetricsAspect :: monitorDuration : LD flag '{}' is ENABLED. Recording metrics for {}.{}.",
          ldFlagName,
          joinPoint.getTarget().getClass().getSimpleName(),
          method.getName());
      Timer timer =
          Timer.builder(METRICS_LONG_RUNNING_THREAD_DURATION_SECONDS)
              .description("Measures the duration of potentially long-running tasks.")
              .tag(METRICS_CLASS, joinPoint.getTarget().getClass().getSimpleName())
              .tag(METRICS_METHOD, method.getName())
              .register(meterRegistry);

      Timer.Sample sample = Timer.start(meterRegistry);

      try {
        return joinPoint.proceed();
      } finally {
        // Stop the timer to record the duration
        long durationNanos = sample.stop(timer);
        String thresholdValueString = annotation.threshold();
        String resolvedThresholdValue = beanFactory.resolveEmbeddedValue(thresholdValueString);
        try {
          // Check if the duration exceeded the configured threshold
          long thresholdNanos =
              TimeUnit.NANOSECONDS.convert(
                  Long.parseLong(resolvedThresholdValue), annotation.unit());
          if (durationNanos > thresholdNanos) {
            incrementExceededCounter(joinPoint);
          }
          log.debug(
              "LongRunningTaskMetricsAspect :: monitorDuration : Metric name: [{}],[{}] Execution of {}.{} finished in {} ms.",
              METRICS_LONG_RUNNING_THREAD_DURATION_SECONDS,
              METRICS_LONG_RUNNING_THREAD_EXCEED_TOTAL,
              joinPoint.getTarget().getClass().getSimpleName(),
              method.getName(),
              TimeUnit.NANOSECONDS.toMillis(durationNanos));
        } catch (NumberFormatException e) {
          // The threshold was invalid. We can't check if it was exceeded,
          // but we should still log the error. The duration metric from the timer is already recorded.
          log.error(
              "LongRunningTaskMetricsAspect :: monitorDuration : Invalid threshold value '{}' on method {}.{}. Must be a valid number. Cannot check if threshold was exceeded.",
              resolvedThresholdValue,
              joinPoint.getTarget().getClass().getSimpleName(),
              method.getName(),
              e);
        }
        }
    } else {
      // LD flag is disabled, skip metrics recording
      log.debug(
          "LongRunningTaskMetricsAspect :: monitorDuration : LD flag '{}' is DISABLED. Skipping metrics for {}.{}.",
          ldFlagName,
          joinPoint.getTarget().getClass().getSimpleName(),
          method.getName());
      return joinPoint.proceed();
    }
  }

  /**
   * Increments the counter for tasks that exceeded their execution threshold. This produces the
   * 'custom_long_running_thread_exceeded_total' metric.
   */
  private void incrementExceededCounter(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    meterRegistry
        .counter(
            METRICS_LONG_RUNNING_THREAD_EXCEED_TOTAL,
            METRICS_CLASS,
            signature.getDeclaringType().getSimpleName(),
            METRICS_METHOD,
            signature.getMethod().getName())
        .increment();
  }
}