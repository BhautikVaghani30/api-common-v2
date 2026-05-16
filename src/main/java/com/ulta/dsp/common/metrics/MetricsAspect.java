/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.metrics;

import static com.ulta.dsp.common.constants.UltaCommonConstants.FLAG_NAME;
import static com.ulta.dsp.common.constants.UltaCommonConstants.METRICS_CLASS;
import static com.ulta.dsp.common.constants.UltaCommonConstants.METRICS_CUSTOM_RESPONSE_TIME;
import static com.ulta.dsp.common.constants.UltaCommonConstants.METRICS_METHOD;
import static com.ulta.dsp.common.constants.UltaCommonConstants.THIRD_PARTY_NAME;

import com.ulta.dsp.common.annotation.Tag;
import com.ulta.dsp.common.annotation.TrackDuration;
import com.ulta.dsp.common.config.LaunchDarklyConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class MetricsAspect {

  private final MeterRegistry meterRegistry;
  @Autowired private LaunchDarklyConfig launchDarklyConfig;

  public MetricsAspect(MeterRegistry meterRegistry, LaunchDarklyConfig launchDarklyConfig) {
    this.meterRegistry = meterRegistry;
    this.launchDarklyConfig = launchDarklyConfig;
  }

  @Around("@annotation(com.ulta.dsp.common.annotation.TrackDuration)")
  public Object trackDuration(ProceedingJoinPoint joinPoint) throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    TrackDuration timedMetric = method.getAnnotation(TrackDuration.class);

    String metricName =
        timedMetric.value().isEmpty() ? METRICS_CUSTOM_RESPONSE_TIME : timedMetric.value();

    Map<String, String> tagsMap =
        Arrays.stream(timedMetric.tags()).collect(Collectors.toMap(Tag::key, Tag::value));
    // find the LD flagName from tag
    String ldFlagName = tagsMap.get(FLAG_NAME);

    boolean ldFlagEnabled = false; // Default to false if no flag is specified
    if (ldFlagName != null) {
      ldFlagEnabled = launchDarklyConfig.flagEnabled(ldFlagName);
    }

    if (ldFlagEnabled) {
      // find the thirdPartyName from tag
      String thirdPartyName = tagsMap.get(THIRD_PARTY_NAME);
      String updatedMetricName =
          StringUtils.isEmpty(thirdPartyName) ? metricName : (metricName + "." + thirdPartyName);
      log.debug(
          "MetricsAspect :: trackDuration : LD flag '{}' is ENABLED. Metric name: [{}], Recording metrics for {}.{}.",
          ldFlagName,
          updatedMetricName,
          joinPoint.getTarget().getClass().getSimpleName(),
          method.getName());
      Timer.Builder timerBuilder =
          Timer.builder(updatedMetricName)
              .description("Execution time for service method")
              .tag(METRICS_METHOD, method.getName())
              .tag(METRICS_CLASS, joinPoint.getTarget().getClass().getSimpleName());

      Timer.Sample timerExecution = Timer.start(meterRegistry);
      try {
        return joinPoint.proceed();
      } finally {
        long durationNanos = timerExecution.stop(timerBuilder.register(meterRegistry));
        log.debug(
            "MetricsAspect :: trackDuration : Execution of {}.{} finished in {} ms.",
            joinPoint.getTarget().getClass().getSimpleName(),
            method.getName(),
            TimeUnit.NANOSECONDS.toMillis(durationNanos));
      }
    } else {
      // LD flag is disabled, skip metrics recording
      log.debug(
          "MetricsAspect :: trackDuration : LD flag '{}' is DISABLED. Skipping metrics for {}.{}.",
          ldFlagName,
          joinPoint.getTarget().getClass().getSimpleName(),
          method.getName());
      return joinPoint.proceed();
    }
  }
}