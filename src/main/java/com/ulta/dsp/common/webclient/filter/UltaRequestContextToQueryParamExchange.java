/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.webclient.filter;

import com.ulta.dsp.common.constants.UltaCommonConstants;
import com.ulta.dsp.common.model.UltaHeaderKeys;
import com.ulta.dsp.common.model.UltaQueryParamKeys;
import com.ulta.dsp.common.model.UltaRequestContext;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Reactive request filter to propagate ULTA context query parameters headers from reactive context
 * to the outgoing WebClient request.
 */
public class UltaRequestContextToQueryParamExchange implements ExchangeFilterFunction {

  @Override
  public Mono<ClientResponse> filter(
      ClientRequest clientRequest, ExchangeFunction exchangeFunction) {
      return Mono.deferContextual(Mono::just)
        .flatMap(
            context -> {
              Optional<UltaRequestContext> requestContext =
                  context.getOrEmpty(UltaCommonConstants.REQUEST_CONTEXT_NAME);

              ClientRequest newRequest =
                  requestContext
                      .map(
                          reqContext -> {
                            if (reqContext != null) {
                              return ClientRequest.from(clientRequest)
                                  .headers(
                                      headers ->
                                          headers.remove(UltaHeaderKeys.X_ULTA_COUNTRY.getValue()))
                                  .headers(
                                      headers ->
                                          headers.remove(UltaHeaderKeys.X_ULTA_LOCALE.getValue()))
                                  .headers(
                                      headers ->
                                          headers.remove(UltaHeaderKeys.X_ULTA_CHANNEL.getValue()))
                                  .headers(
                                      headers ->
                                          headers.remove(UltaHeaderKeys.X_ULTA_DEVICE.getValue()))
                                  .headers(
                                      headers ->
                                          headers.remove(
                                              UltaHeaderKeys.X_ULTA_ASSOCIATE_ID.getValue()))
                                  .headers(
                                      headers ->
                                          headers.remove(UltaHeaderKeys.X_ULTA_STORE_ID.getValue()))
                                  .headers(
                                      headers ->
                                          headers.remove(UltaHeaderKeys.X_ULTA_TRUE_CLIENT_IP.getValue()))
                                  .headers(
                                          headers ->
                                              headers.remove(UltaHeaderKeys.X_AKAMAI_DEVICE_CHARACTERISTICS.getValue()))
                                  .url(
                                      UriComponentsBuilder.fromUri(clientRequest.url())
                                          .queryParam(
                                              UltaQueryParamKeys.X_ULTA_COUNTRY.getValue(),
                                              reqContext.getCountry())
                                          .queryParam(
                                              UltaQueryParamKeys.X_ULTA_LOCALE.getValue(),
                                              Optional.ofNullable(reqContext.getLocale())
                                                  .map(
                                                      locale ->
                                                          locale
                                                              .toString()
                                                              .toLowerCase()
                                                              .replace("_", "-"))
                                                  .orElse(""))
                                          .queryParam(
                                              UltaQueryParamKeys.X_ULTA_CHANNEL.getValue(),
                                              Optional.ofNullable(reqContext.getChannel()))
                                          .queryParam(
                                              UltaQueryParamKeys.X_ULTA_DEVICE.getValue(),
                                              Optional.ofNullable(reqContext.getDevice()))
                                          .queryParam(
                                              UltaQueryParamKeys.X_ULTA_STORE_ID.getValue(),
                                              Optional.ofNullable(reqContext.getStoreId()))
                                          .queryParam(
                                              UltaQueryParamKeys.X_ULTA_ASSOCIATE_ID.getValue(),
                                              Optional.ofNullable(reqContext.getAssociateId()))
                                          .queryParam(
                                              UltaQueryParamKeys.X_ULTA_TRUE_CLIENT_IP.getValue(),
                                              Optional.ofNullable(reqContext.getTrueClientIp()))
                                          .queryParam(
                                                  UltaQueryParamKeys.X_AKAMAI_DEVICE_CHARACTERISTICS.getValue(),
                                                  Optional.ofNullable(reqContext.getDeviceCharacteristics()))
                                          .build()
                                          .toUri())
                                  .build();
                            }
                            return clientRequest;
                          })
                      .orElse(clientRequest);

              return exchangeFunction.exchange(newRequest);
            });
  }
}