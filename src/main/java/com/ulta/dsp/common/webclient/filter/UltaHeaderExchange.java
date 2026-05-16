/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.webclient.filter;

import com.ulta.dsp.common.constants.UltaCommonConstants;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

/**
 * Reactive request filter to propagate HTTP headers from reactive context to the outgoing WebClient
 * request.
 */
public class UltaHeaderExchange implements ExchangeFilterFunction {

  @Override
  public Mono<ClientResponse> filter(
      ClientRequest clientRequest, ExchangeFunction exchangeFunction) {
      return Mono.deferContextual(Mono::just)
        .flatMap(
            context -> {
              Optional<Map<String, String>> headerMap =
                  context.getOrEmpty(UltaCommonConstants.HEADERS_CONTEXT_NAME);

              ClientRequest newRequest =
                  headerMap
                      .map(
                          headers -> {
                            if (!headers.isEmpty()) {
                              return ClientRequest.from(clientRequest)
                                  .headers(
                                      httpHeaders ->
                                          headers.forEach(
                                              httpHeaders::add))
                                  .build();
                            }
                            return clientRequest;
                          })
                      .orElse(clientRequest);

              return exchangeFunction.exchange(newRequest);
            });
  }
}