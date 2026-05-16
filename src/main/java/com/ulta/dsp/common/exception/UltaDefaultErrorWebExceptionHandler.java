/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.exception;

import com.ulta.dsp.common.model.UltaMessage;
import com.ulta.dsp.common.model.UltaMeta;
import com.ulta.dsp.common.model.UltaResponse;
import com.ulta.dsp.common.model.UltaResponseType;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

public class UltaDefaultErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

  public static final String ULTA_REQUEST_CONTEXT_ERROR_MESSAGE_CODE =
      "E_C_CONTEXT_1000_INVALID-REQUEST-CONTEXT";
  public static final String ULTA_GLOBAL_ERROR_MESSAGE_CODE = "E_C_GLOBAL_1000_REQUEST-FAILED";

  public UltaDefaultErrorWebExceptionHandler(
      ErrorAttributes errorAttributes,
      WebProperties resourceProperties,
      ErrorProperties errorProperties,
      ApplicationContext applicationContext) {
    super(errorAttributes, resourceProperties.getResources(), errorProperties, applicationContext);
  }

  /**
   * Render the error information as a JSON payload.
   *
   * @param request the current request
   * @return a {@code Publisher} of the HTTP response
   */
  @Override
  protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    ErrorAttributeOptions errorAttributeOptions =
        ErrorAttributeOptions.defaults()
            .including(ErrorAttributeOptions.Include.MESSAGE)
            .including(ErrorAttributeOptions.Include.EXCEPTION)
            .including(ErrorAttributeOptions.Include.STACK_TRACE);
    return ServerResponse.status(getHttpStatus(getErrorAttributes(request, errorAttributeOptions)))
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(resolveCustomErrorResponse(request)));
  }

  private Object resolveCustomErrorResponse(ServerRequest request) {
    return Optional.ofNullable(getError(request)).map(this::processResponse);
  }

  private UltaResponse<?> processResponse(Throwable error) {
    if (error instanceof UltaRequestContextException) {
      return UltaResponse.builder()
          .meta(
              UltaMeta.builder()
                  .status(UltaResponseType.FAIL.getValue())
                  .timestamp(Instant.now().toString())
                  .build())
          .messages(
              Arrays.asList(
                  UltaMessage.builder()
                      .code(ULTA_REQUEST_CONTEXT_ERROR_MESSAGE_CODE)
                      .type(UltaMessage.UltaMessageType.ERROR.getValue())
                      .build()))
          .build();
    } else {
      return UltaResponse.builder()
          .meta(
              UltaMeta.builder()
                  .status(UltaResponseType.FAIL.getValue())
                  .timestamp(Instant.now().toString())
                  .build())
          .messages(
              Arrays.asList(
                  UltaMessage.builder()
                      .code(ULTA_GLOBAL_ERROR_MESSAGE_CODE)
                      .type(UltaMessage.UltaMessageType.ERROR.getValue())
                      .build()))
          .build();
    }
  }
}