/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.metrics;

import static com.ulta.dsp.common.constants.UltaCommonConstants.FLAG_NAME;
import static com.ulta.dsp.common.constants.UltaCommonConstants.METRICS_CLASS;
import static com.ulta.dsp.common.constants.UltaCommonConstants.METRICS_CUSTOM_RESPONSE_TIME_OUT_TOTAL;
import static com.ulta.dsp.common.constants.UltaCommonConstants.METRICS_METHOD;

import com.ulta.dsp.common.annotation.Tag;
import com.ulta.dsp.common.annotation.Timeout;
import com.ulta.dsp.common.config.LaunchDarklyConfig;
import io.micrometer.core.instrument.MeterRegistry;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Aspect
@Component
public class TimeoutMetricsAspect {

  private final MeterRegistry meterRegistry;
  private final ConfigurableBeanFactory beanFactory;
  @Autowired private LaunchDarklyConfig launchDarklyConfig;

  public TimeoutMetricsAspect(MeterRegistry meterRegistry, ConfigurableBeanFactory beanFactory) {
    this.meterRegistry = meterRegistry;
      this.beanFactory = beanFactory;
  }

  @Around("@annotation(com.ulta.dsp.common.annotation.Timeout)")
  public Object trackAndEnforceTimeout(ProceedingJoinPoint joinPoint) throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    Timeout timeoutAnnotation = method.getAnnotation(Timeout.class);

    Map<String, String> tagsMap =
        Arrays.stream(timeoutAnnotation.tags()).collect(Collectors.toMap(Tag::key, Tag::value));
    // find the LD flagName from tag
    String ldFlagName = tagsMap.get(FLAG_NAME);

    boolean ldFlagEnabled = false; // Default to false if no flag is specified
    if (ldFlagName != null) {
      ldFlagEnabled = launchDarklyConfig.flagEnabled(ldFlagName);
    }

    if (ldFlagEnabled) {
      Duration timeoutDuration;
      String timeoutValue = timeoutAnnotation.value();
      String resolvedTimeoutValue = beanFactory.resolveEmbeddedValue(timeoutValue);
      try {
        timeoutDuration =
                Duration.of(Long.parseLong(resolvedTimeoutValue), timeoutAnnotation.unit().toChronoUnit());
      } catch (NumberFormatException e) {
        log.error(
            "TimeoutMetricsAspect :: trackAndEnforceTimeout : Invalid timeout value '{}' on method {}.{}. Must be a valid number. Proceeding without timeout enforcement.",
            resolvedTimeoutValue,
            joinPoint.getTarget().getClass().getSimpleName(),
            method.getName(),
            e);
        // Proceed without a timeout, as we cannot enforce one.
        return joinPoint.proceed();
      }
      log.debug(
          "TimeoutMetricsAspect :: trackAndEnforceTimeout : LD flag '{}' is ENABLED. Applying timeout of Value: {}, Unit: {} to {}.{}.",
          ldFlagName,
          resolvedTimeoutValue,
          timeoutAnnotation.unit(),
          joinPoint.getTarget().getClass().getSimpleName(),
          method.getName());

      Object result = joinPoint.proceed();

      // Apply timeout logic for reactive types
      if (result instanceof Mono) {
        return ((Mono<?>) result)
            .timeout(timeoutDuration)
            .doOnError(TimeoutException.class, e -> incrementTimeoutCounter(joinPoint));
      } else if (result instanceof Flux) {
        return ((Flux<?>) result)
            .timeout(timeoutDuration)
            .doOnError(TimeoutException.class, e -> incrementTimeoutCounter(joinPoint));
      }

      // If the return type is not reactive, this aspect will have no effect.
      return result;
    } else {
      // LD flag is disabled, skip metrics recording
      log.debug(
          "TimeoutMetricsAspect :: trackAndEnforceTimeout : LD flag '{}' is not present or is DISABLED. Skipping timeout enforcement for {}.{}.",
          ldFlagName,
          joinPoint.getTarget().getClass().getSimpleName(),
          method.getName());
      return joinPoint.proceed();
    }
  }

  /**
   * Finds or creates a counter and increments it. The counter is tagged with the class and method
   * where the timeout occurred.
   */
  private void incrementTimeoutCounter(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    log.warn(
        "TimeoutMetricsAspect :: incrementTimeoutCounter : Metric name: [{}] Timeout detected for {}.{}! Incrementing timeout counter.",
        METRICS_CUSTOM_RESPONSE_TIME_OUT_TOTAL,
        signature.getDeclaringType().getSimpleName(),
        signature.getMethod().getName());
    meterRegistry
        .counter(
            METRICS_CUSTOM_RESPONSE_TIME_OUT_TOTAL,
            METRICS_CLASS,
            signature.getDeclaringType().getSimpleName(),
            METRICS_METHOD,
            signature.getMethod().getName())
        .increment();
  }
}