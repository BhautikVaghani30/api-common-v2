/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.utils;

import com.ulta.dsp.common.constants.UltaCommonConstants;
import com.ulta.dsp.common.model.UltaHeaderKeys;
import com.ulta.dsp.common.model.UltaRequestContext;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.util.context.Context;

import static com.ulta.dsp.common.constants.UltaCommonConstants.COR_UNIQUEID;
import static com.ulta.dsp.common.constants.UltaCommonConstants.REQUEST_CONTEXT_NAME;


/**
 * Utility class for managing and manipulating the Reactor {@link Context}.
 * <p>
 * This class provides helper methods to interact with objects stored within the reactive context,
 * such as the {@link UltaRequestContext}.
 */
public class ContextUtils {

  /**
   * Sets a new correlation ID in the {@link UltaRequestContext} within the provided Reactor {@link Context}.
   * It will be invoked from Web Filter for every request
   * If a {@link UltaRequestContext} is not present, the context remains unchanged.
   *
   * @param context The Reactor {@link Context} to modify.
   * @return The updated Reactor {@link Context} with the correlation ID set.
   */
  public static Context setCorrelationIdWithContext(Context context) {
    Optional<UltaRequestContext> requestContext =
            context.getOrEmpty(UltaCommonConstants.REQUEST_CONTEXT_NAME);
    if (!requestContext.isPresent()) {
      return context;
    }
    String existingCorId = Optional.ofNullable(requestContext.get().getCorrelationId())
              .filter(StringUtils::isNotBlank)
              .orElse(null);
      String correlationId;
      if (existingCorId != null) {
        correlationId = existingCorId;
      } else {
        correlationId = generateUniqueId( null, null);
        requestContext.get().setCorrelationId(correlationId);
      }
      return setCorrelationIdWithHeaders(context, correlationId);
  }




  /**
   * Generates a unique ID string, optionally prefixed with a flow name and including additional details.
   * The ID can be designated as a general unique ID.
   *
   * @param flowName An optional name for the flow, which will be prepended to the ID if provided.
   * @param flowObjectDetails Optional additional string details to append to the generated ID.
   * @return A unique ID string.
   */
  private static String generateUniqueId(String flowName, String correlationId, String... flowObjectDetails) {
    StringBuilder sb = new StringBuilder(64);
    // Prefix: custom or default
    if (flowName != null && !flowName.isBlank()) {
      sb.append(flowName).append("_");
    }
    if(correlationId != null) {
      sb.append(correlationId);
    }
    else {
      sb.append(COR_UNIQUEID);
      sb.append(":");
      sb.append(UUID.randomUUID().toString());
    }
    if (flowObjectDetails != null && flowObjectDetails.length > 0) {
      Arrays.stream(flowObjectDetails)
              .filter(StringUtils::isNotBlank)
              .forEach(sb::append);
    }
    return sb.toString();
  }

  /**
   * Sets the correlation ID in the header map within the Reactor {@link Context} and also adds it to the MDC context.
   * @param context The Reactor {@link Context} to modify.
   * @param correlationId The correlation ID to set.
   * @return The updated Reactor {@link Context}.
   */
  private static Context setCorrelationIdWithHeaders(Context context, String correlationId) {
    Map<String, String> headerMap = context
            .<Map<String, String>>getOrEmpty(UltaCommonConstants.HEADERS_CONTEXT_NAME)
            .map(existing -> {
              Map<String, String> copy = new HashMap<>(existing);
              copy.put(UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue(), correlationId);
              return copy;
            })
            .orElseGet(() -> {
              Map<String, String> newMap = new HashMap<>();
              newMap.put(UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue(), correlationId);
              return newMap;
            });

    return context
            .put(UltaCommonConstants.HEADERS_CONTEXT_NAME, headerMap)
            .put(UltaCommonConstants.MDC_KEY_CORRELATION_ID, correlationId);
  }

  /**
   * Keep the original httpHeaders values
   * Adds the correlation ID from the Reactor {@link Context} to the provided header map if it's not already present.
   *
   * @param context The Reactor {@link Context} from which to retrieve the correlation ID.
   * @return A new map containing all original headers and the correlation ID if it was added.
   */
  public static Map<String, String> addCorrelationIdToHeader(Context context) {
    Map<String, String> enriched = new HashMap<>(
            context.<Map<String, String>>getOrEmpty(UltaCommonConstants.HEADERS_CONTEXT_NAME)
                    .orElseGet(Collections::emptyMap)
    );
    String existingValue = enriched.get(UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue());
    if (existingValue == null || existingValue.isBlank()) {
      context.getOrEmpty(UltaCommonConstants.MDC_KEY_CORRELATION_ID)
              .map(Object::toString)           // safe conversion from Object
              .filter(StringUtils::isNotBlank)
              .ifPresent(correlationId ->
                      enriched.put(UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue(), correlationId)
              );
    }
    return enriched;
  }

  /**
   * Keep the original httpHeaders values Adds the correlation ID from the Reactor {@link Context}
   * to the provided header map and ulta-request-context if it's not already present.
   *
   * @param context The Reactor {@link Context} from which to retrieve the correlation ID.
   * @param exchange ServerWebExchange
   * @return A new map containing all original headers and the correlation ID if it was added.
   */
  public static Map<String, String> addCorrelationIdToHeader(
      Context context, ServerWebExchange exchange) {
    Map<String, String> enriched =
        new HashMap<>(
            context
                .<Map<String, String>>getOrEmpty(UltaCommonConstants.HEADERS_CONTEXT_NAME)
                .orElseGet(Collections::emptyMap));
    Optional<UltaRequestContext> ultaRequestContext = context.getOrEmpty(REQUEST_CONTEXT_NAME);

    String existingValue = enriched.get(UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue());
    if (existingValue == null || existingValue.isBlank()) {
      context
          .getOrEmpty(UltaCommonConstants.MDC_KEY_CORRELATION_ID)
          .map(Object::toString) // safe conversion from Object
          .filter(StringUtils::isNotBlank)
          .ifPresent(
              correlationId ->
                  enriched.put(UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue(), correlationId));
    }
    String correlationIdFromHeaderExchange =
        Optional.ofNullable(exchange.getRequest())
            .map(request -> request.getHeaders())
            .map(
                httpHeaders ->
                    httpHeaders.getFirst(UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue()))
            .orElse(null);
    if (null == enriched.get(UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue())
        && null != correlationIdFromHeaderExchange) {
      enriched.put(
          UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue(), correlationIdFromHeaderExchange);
    }
    if (ultaRequestContext.isPresent()
        && null == ultaRequestContext.get().getCorrelationId()
        && null != correlationIdFromHeaderExchange) {
      ultaRequestContext.get().setCorrelationId(correlationIdFromHeaderExchange);
    }
    return enriched;
  }
}