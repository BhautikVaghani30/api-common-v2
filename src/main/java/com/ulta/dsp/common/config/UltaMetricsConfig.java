/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.micrometer.core.instrument.logging.LoggingRegistryConfig;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression(
        "${ulta.webclient.metrics.logging.enabled:false} and '${management.endpoints.web.exposure.include}'.contains('metrics')"
)
class UltaMetricsConfig {

    @Value("${ulta.metrics.config.interval:300}")
    private long metricsIntervalSecs;

  @Bean
  LoggingMeterRegistry loggingMeterRegistry() {
    return  new LoggingMeterRegistry(new LoggingRegistryConfig() {
          @Override
          public Duration step() {
              return Duration.ofSeconds(metricsIntervalSecs);
          }

          @Override
          public String get(String key) {
              return null;
          }
      }, Clock.SYSTEM);
  }
}