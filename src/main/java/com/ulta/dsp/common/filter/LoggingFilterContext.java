/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.filter;

import com.ulta.dsp.common.config.LaunchDarklyConfig;
import com.ulta.dsp.common.constants.UltaCommonConstants;
import com.ulta.dsp.common.model.UltaHeaderKeys;
import com.ulta.dsp.common.model.UltaQueryParamKeys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.function.Consumer;

import static com.ulta.dsp.common.constants.UltaCommonConstants.LD_ENABLE_CORRELATION_ID_FLAG;
import static com.ulta.dsp.common.constants.UltaCommonConstants.MDC_KEY_CORRELATION_ID;

@Component
public class LoggingFilterContext implements WebFilter {

    private LaunchDarklyConfig launchDarklyConfig;
    public LoggingFilterContext(LaunchDarklyConfig launchDarklyConfig){
        this.launchDarklyConfig = launchDarklyConfig;
    }

  @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        class MyContextContainer {
            private ContextView contextView;

            public ContextView getContextView() {
                return contextView;
            }

            public void setContextView(ContextView contextView) {
                this.contextView = contextView;
            }
        }

        MyContextContainer container = new MyContextContainer();

        Mono<Void> filter = chain.filter(exchange);

    return filter
        .doOnEach(
            new Consumer<Signal<Void>>() {
              @Override
              public void accept(Signal<Void> voidSignal) {
                if (voidSignal.getContextView() != null
                    && voidSignal.getContextView().hasKey(Context.class)) {
                  container.setContextView(voidSignal.getContextView().get(Context.class));
                }
              }
            })
        .contextWrite(
            context ->
                context.put(
                    UltaCommonConstants.MDC_KEY_DIGITAL_ID,
                    StringUtils.defaultIfBlank(
                        exchange
                            .getRequest()
                            .getHeaders()
                            .getFirst(UltaHeaderKeys.X_ULTA_USER_ID.getValue()),
                        "")))
        .contextWrite(
            context ->
                context.put(
                    UltaCommonConstants.MDC_KEY_SESSION_ID,
                    StringUtils.defaultIfBlank(
                        exchange
                            .getRequest()
                            .getHeaders()
                            .getFirst(UltaHeaderKeys.X_ULTA_SESSION_ID.getValue()),
                        "")))
        .contextWrite(
            context -> {
              boolean ldFlagEnabled = false; // Default to false if no flag is specified
              ldFlagEnabled = launchDarklyConfig.flagEnabled(LD_ENABLE_CORRELATION_ID_FLAG);
              if (ldFlagEnabled) {
                // Try header first, then query param
                String correlationId =
                    StringUtils.defaultIfBlank(
                        exchange
                            .getRequest()
                            .getHeaders()
                            .getFirst(UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue()),
                        exchange
                            .getRequest()
                            .getQueryParams()
                            .getFirst(UltaQueryParamKeys.X_ULTA_CORRELATION_ID.getValue()));
                // Only update context if we actually found a value
                if (StringUtils.isNotBlank(correlationId)) {
                  return context.put(MDC_KEY_CORRELATION_ID, correlationId);
                }
              }
              return context;
            });
  }
}