/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.config;

import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
public class UltaContextConfig {

  private static final String REQUEST_CONTEXT_REACTOR_KEY = UltaContextConfig.class.getName();

  @PostConstruct
  private void contextOperatorHook() {
    Hooks.onEachOperator(
        REQUEST_CONTEXT_REACTOR_KEY,
        Operators.lift(
            (scannable, coreSubscriber) -> new UltaRequestContextLifter<>(coreSubscriber)));
  }

  @PreDestroy
  private void cleanupHook() {
    Hooks.resetOnEachOperator(REQUEST_CONTEXT_REACTOR_KEY);
  }
}