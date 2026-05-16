/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.webclient.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * Reactive request filter to log HTTP request and response details of the outgoing WebClient
 * request.
 */
@Slf4j
public class UltaWebClientRequestResponseLogger implements ExchangeFilterFunction {

  private boolean loggingEnabled;

  public UltaWebClientRequestResponseLogger(boolean loggingEnabled) {
    this.loggingEnabled = loggingEnabled;
  }

  @Override
  public Mono<ClientResponse> filter(
      ClientRequest clientRequest, ExchangeFunction exchangeFunction) {

    if (!loggingEnabled) {
      return exchangeFunction.exchange(clientRequest);
    }
    log.info(
        "Request: method={}, url={}, queryString={}, headers={}",
        clientRequest.method(),
        clientRequest.url(),
        clientRequest.url().getQuery(),
        clientRequest.headers());

    return exchangeFunction
        .exchange(clientRequest)
        .doOnSuccess(
            clientResponse ->
                log.info(
                    "Response:  url={}, headers={}, status={}",
                    clientRequest.url(),
                    clientResponse.headers().asHttpHeaders(),
                    clientResponse.statusCode()))
        .doOnError(
            throwable -> {
              log.info("Response:  url={}, status={} ", clientRequest.url(), 500);
              log.error("Error = ", throwable);
            });
  }
}