/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UltaRequestResponseLoggingFilter implements WebFilter {

  @Value("${ulta.logging.requestresponse.enabled:false}")
  private boolean enabled;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

    if (!enabled) {
      return chain.filter(exchange);
    }

    final long startTime = System.currentTimeMillis();

    log.info(
        "Request: method={}, uri={}, headers={}",
        exchange.getRequest().getMethod(),
        exchange.getRequest().getURI().getPath(),
        exchange.getRequest().getHeaders());

    ServerWebExchangeDecorator exchangeDecorator =
        new ServerWebExchangeDecorator(exchange) {
          @Override
          public ServerHttpRequest getRequest() {
            return new UltaRequestLoggingInterceptor(super.getRequest());
          }

          @Override
          public ServerHttpResponse getResponse() {
            return new UltaResponseLoggingInterceptor(super.getResponse(), startTime);
          }
        };
    return chain
        .filter(exchangeDecorator)
        .doOnSuccess(
            aVoid -> {
              logResponse(
                  startTime,
                  exchangeDecorator.getResponse(),
                  exchangeDecorator.getResponse().getStatusCode().value());
            })
        .doOnError(
            throwable -> {
              logResponse(startTime, exchangeDecorator.getResponse(), 500);
            });
  }

  private void logResponse(long startTime, ServerHttpResponse response, int overriddenStatus) {
    final long duration = System.currentTimeMillis() - startTime;
    log.info(
        "Response({} ms): status={}, headers={}",
        duration,
        overriddenStatus,
        response.getHeaders());
  }
}