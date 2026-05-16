/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.filter;

import static com.ulta.dsp.common.constants.UltaCommonConstants.LD_ENABLE_CORRELATION_ID_FLAG;

import com.ulta.dsp.common.config.LaunchDarklyConfig;
import com.ulta.dsp.common.constants.UltaCommonConstants;
import com.ulta.dsp.common.model.UltaHeaderKeys;
import com.ulta.dsp.common.utils.ContextUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(1)
@Component
public class UltaHeaderContextFilter implements WebFilter {

  private LaunchDarklyConfig launchDarklyConfig;

  public UltaHeaderContextFilter(LaunchDarklyConfig launchDarklyConfig) {
    this.launchDarklyConfig = launchDarklyConfig;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    boolean ldFlagEnabled = false; // Default to false if no flag is specified
    ldFlagEnabled = launchDarklyConfig.flagEnabled(LD_ENABLE_CORRELATION_ID_FLAG);
    boolean finalLdFlagEnabled = ldFlagEnabled;
    return chain
        .filter(exchange)
        .contextWrite(
            context -> {
              if (finalLdFlagEnabled) {
                Map<String, String> headerMap =
                    ContextUtils.addCorrelationIdToHeader(context, exchange);
                Arrays.stream(UltaHeaderKeys.values())
                    .forEach(
                        header -> {
                          String key = header.getValue();
                          String value =
                              Optional.ofNullable(exchange.getRequest())
                                  .map(request -> request.getHeaders())
                                  .map(httpHeaders -> httpHeaders.getFirst(key))
                                  .orElse(null);
                          if (!CollectionUtils.isEmpty(headerMap)
                              && UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue().equals(key)) {
                            String existing = headerMap.get(key);
                            if (existing != null && !existing.isBlank()) {
                              return; // skip putting it
                            }
                          }
                          headerMap.put(key, value);
                        });
                context = context.put(UltaCommonConstants.HEADERS_CONTEXT_NAME, headerMap);

              } else {
                Map<String, String> headerMap = new HashMap<>();
                Arrays.stream(UltaHeaderKeys.values())
                    .forEach(
                        header ->
                            headerMap.put(
                                header.getValue(),
                                exchange.getRequest().getHeaders().getFirst(header.getValue())));
                context = context.put(UltaCommonConstants.HEADERS_CONTEXT_NAME, headerMap);
              }

              return context;
            });
  }
}