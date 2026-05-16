/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.filter;

import com.ulta.dsp.common.constants.UltaCommonConstants;
import com.ulta.dsp.common.exception.UltaRequestContextException;
import com.ulta.dsp.common.model.UltaCountry;
import com.ulta.dsp.common.model.UltaHeaderKeys;
import com.ulta.dsp.common.model.UltaHeaderValues;
import com.ulta.dsp.common.model.UltaQueryParamKeys;
import com.ulta.dsp.common.model.UltaRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UltaRequestContextFilter implements WebFilter {

  @Value("${ulta.request.context.country.required:true}")
  private boolean countryRequired;

  @Value(
      "${ulta.request.context.ignorepatterns.required:.*(favicon|\\.css|\\.js|\\.map|\\.yaml|\\.html|/v(\\d)*/actuator|api-docs|swagger).*}")
  private String ignorePatterns;

  @Value("${ulta.request.context.orchapipattern:/v(\\d)*/orch/.*}")
  private String orchAPIPathPattern;

  @Value("${ulta.request.context.coreapipattern:/v(\\d)*/core/.*}")
  private String coreAPIPathPattern;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

    if (ignorePatterns != null
        && exchange.getRequest().getURI().getPath().matches(ignorePatterns)) {
      return chain.filter(exchange);
    }

    return chain
        .filter(exchange)
        .contextWrite(
            context -> {
              Map<String, String> attributeMap = new HashMap<>();
              UltaRequestContext ultaRequestContext = null;
              UltaHeaderValues ultaHeaders = null;

              if (coreAPIPathPattern != null
                  && exchange.getRequest().getURI().getPath().matches(coreAPIPathPattern)) {
                UltaCommonConstants.ULTA_QUERYPARAM_CONTEXT_PROPERTIES.forEach(
                    attribute ->
                        attributeMap.put(
                            attribute, exchange.getRequest().getQueryParams().getFirst(attribute)));

                ultaHeaders =
                    UltaHeaderValues.builder()
                        .country(
                            attributeMap.remove((UltaQueryParamKeys.X_ULTA_COUNTRY.getValue())))
                        .languageTag(
                            attributeMap.remove(UltaQueryParamKeys.X_ULTA_LOCALE.getValue()))
                        .deliveryMethod(
                            attributeMap.remove(
                                UltaQueryParamKeys.X_ULTA_DELIVERY_METHOD.getValue()))
                        .channel(attributeMap.remove(UltaQueryParamKeys.X_ULTA_CHANNEL.getValue()))
                        .device(attributeMap.remove(UltaQueryParamKeys.X_ULTA_DEVICE.getValue()))
                        .storeId(attributeMap.remove(UltaQueryParamKeys.X_ULTA_STORE_ID.getValue()))
                        .associateId(
                            attributeMap.remove(UltaQueryParamKeys.X_ULTA_ASSOCIATE_ID.getValue()))
                        .trueClientIp(attributeMap.remove(UltaQueryParamKeys.X_ULTA_TRUE_CLIENT_IP.getValue()))
                        .deviceCharacteristics(attributeMap.remove(UltaQueryParamKeys.X_AKAMAI_DEVICE_CHARACTERISTICS.getValue()))
                        .correlationId(attributeMap.remove(UltaQueryParamKeys.X_ULTA_CORRELATION_ID.getValue()))
                        .build();

                log.debug(
                    "{}={}, {}={} in query.",
                    UltaQueryParamKeys.X_ULTA_COUNTRY.getValue(),
                    ultaHeaders.getCountry(),
                    UltaQueryParamKeys.X_ULTA_LOCALE.getValue(),
                    ultaHeaders.getLanguageTag());

                ultaRequestContext = createUltaRequestContextForCoreLayer(ultaHeaders);

              } else if (orchAPIPathPattern != null
                  && exchange.getRequest().getURI().getPath().matches(orchAPIPathPattern)) {

                UltaCommonConstants.ULTA_HEADER_CONTEXT_PROPERTIES.forEach(
                    attribute ->
                        attributeMap.put(
                            attribute, exchange.getRequest().getHeaders().getFirst(attribute)));

                ultaHeaders =
                    UltaHeaderValues.builder()
                        .country(attributeMap.remove((UltaHeaderKeys.X_ULTA_COUNTRY.getValue())))
                        .languageTag(attributeMap.remove(UltaHeaderKeys.X_ULTA_LOCALE.getValue()))
                        .userId(attributeMap.remove(UltaHeaderKeys.X_ULTA_USER_ID.getValue()))
                        .sessionId(attributeMap.remove(UltaHeaderKeys.X_ULTA_SESSION_ID.getValue()))
                        .authStatus(
                            attributeMap.remove(UltaHeaderKeys.X_ULTA_AUTH_STATUS.getValue()))
                        .storeId(attributeMap.remove(UltaHeaderKeys.X_ULTA_STORE_ID.getValue()))
                        .associateId(
                            attributeMap.remove(UltaHeaderKeys.X_ULTA_ASSOCIATE_ID.getValue()))
                        .channel(attributeMap.remove(UltaHeaderKeys.X_ULTA_CHANNEL.getValue()))
                        .device(attributeMap.remove(UltaHeaderKeys.X_ULTA_DEVICE.getValue()))
                        .trueClientIp(attributeMap.remove(UltaHeaderKeys.X_ULTA_TRUE_CLIENT_IP.getValue()))
                        .deviceCharacteristics(attributeMap.remove(UltaHeaderKeys.X_AKAMAI_DEVICE_CHARACTERISTICS.getValue()))
                        .loyaltyMemberId(attributeMap.remove(UltaHeaderKeys.X_ULTA_LOYALTY_MEMBER_ID.getValue()))
                        .loyaltyMemberStatus(attributeMap.remove(UltaHeaderKeys.X_ULTA_LOYALTY_MEMBER_STATUS.getValue()))
                        .correlationId(attributeMap.remove(UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue()))
                        .build();

                log.debug("UltaHeaderValues {}", ultaHeaders);

                ultaRequestContext = createUltaRequestContextForOrchLayer(ultaHeaders);
              }

              return ultaRequestContext != null
                      ? context.put(UltaCommonConstants.REQUEST_CONTEXT_NAME, ultaRequestContext)
                      : context;
            });
  }

  private UltaRequestContext createUltaRequestContextForOrchLayer(UltaHeaderValues ultaHeaders) {

    Locale locale = null;
    UltaCountry requestCountry = null;

    if (ultaHeaders != null && !StringUtils.isBlank(ultaHeaders.getCountry())) {

      if (UltaCommonConstants.ULTA_CONTEXT_SUPPORTED_COUNTRIES.stream()
          .noneMatch(reqCountry -> reqCountry.getValue().equals(ultaHeaders.getCountry()))) {
        throw new UltaRequestContextException(
            String.format(
                "Country \"%s\" is not supported in the request context. Supported countries = %s",
                ultaHeaders.getCountry(), UltaCommonConstants.ULTA_CONTEXT_SUPPORTED_COUNTRIES));
      }

      requestCountry = UltaCountry.valueOf(ultaHeaders.getCountry().toUpperCase());
      locale =
          Optional.ofNullable(ultaHeaders.getLanguageTag())
              .map(Locale::forLanguageTag)
              .orElse(null);

      if (locale == null) {
        log.debug("Error parsing language key.");
      }

    } else {
      if (countryRequired) {
        throw new UltaRequestContextException("Country is required in request context.");
      } else {
        requestCountry = UltaCountry.DEFAULT;
        locale = Locale.forLanguageTag(UltaCommonConstants.DEFAULT_COUNTRY_LOCALE);
        log.debug(
            "Using default country and locale. {}={}, {}={} for context.",
            UltaHeaderKeys.X_ULTA_COUNTRY.getValue(),
            requestCountry,
            UltaHeaderKeys.X_ULTA_LOCALE.getValue(),
            UltaCommonConstants.DEFAULT_COUNTRY_LOCALE);
      }
    }

    if (ultaHeaders != null && StringUtils.isBlank(ultaHeaders.getUserId())) {
      throw new UltaRequestContextException("userId is required in request context.");
    }

    if (ultaHeaders != null && StringUtils.isBlank(ultaHeaders.getSessionId())) {
      throw new UltaRequestContextException("sessionId is required in request context.");
    }

    if (ultaHeaders != null && StringUtils.isBlank(ultaHeaders.getAuthStatus())) {
      throw new UltaRequestContextException("authStatus is required in request context.");
    }

    return UltaRequestContext.builder()
        .country(requestCountry)
        .locale(locale)
        .userId(ultaHeaders.getUserId())
        .sessionId(ultaHeaders.getSessionId())
        .authStatus(ultaHeaders.getAuthStatus())
        .channel(ultaHeaders.getChannel())
        .device(ultaHeaders.getDevice())
        .storeId(ultaHeaders.getStoreId())
        .associateId(ultaHeaders.getAssociateId())
        .trueClientIp(ultaHeaders.getTrueClientIp())
        .deviceCharacteristics(ultaHeaders.getDeviceCharacteristics())
        .loyaltyMemberId(ultaHeaders.getLoyaltyMemberId())
        .loyaltyMemberStatus(ultaHeaders.getLoyaltyMemberStatus())
        .correlationId(ultaHeaders.getCorrelationId())
        .build();
  }

  private UltaRequestContext createUltaRequestContextForCoreLayer(UltaHeaderValues ultaHeaders) {

    Locale locale = null;
    UltaCountry requestCountry = null;
    String deliveryMethod = null;
    String channel = null;
    String device = null;
    String storeId = null;
    String associateId = null;
    String trueClientIp = null;
    String deviceCharacteristics = null;
    String correlationId = null;



    if (ultaHeaders != null && !StringUtils.isBlank(ultaHeaders.getCountry())) {

      if (UltaCommonConstants.ULTA_CONTEXT_SUPPORTED_COUNTRIES.stream()
          .noneMatch(reqCountry -> reqCountry.getValue().equals(ultaHeaders.getCountry()))) {
        throw new UltaRequestContextException(
            String.format(
                "Country \"%s\" is not supported in the request context. Supported countries = %s",
                ultaHeaders.getCountry(), UltaCommonConstants.ULTA_CONTEXT_SUPPORTED_COUNTRIES));
      }

      requestCountry = UltaCountry.valueOf(ultaHeaders.getCountry().toUpperCase());
      locale =
          Optional.ofNullable(ultaHeaders.getLanguageTag())
              .map(Locale::forLanguageTag)
              .orElse(null);

      if (locale == null) {
        log.debug("Error parsing language key.");
      }
      if (!StringUtils.isBlank(ultaHeaders.getDeliveryMethod())) {
        deliveryMethod = ultaHeaders.getDeliveryMethod().toUpperCase();
      }

    } else {
      if (countryRequired) {
        throw new UltaRequestContextException("Country is required in request context.");
      } else {
        requestCountry = UltaCountry.DEFAULT;
        locale = Locale.forLanguageTag(UltaCommonConstants.DEFAULT_COUNTRY_LOCALE);
        log.debug(
            "Using default country and locale. {}={}, {}={} for context.",
            UltaHeaderKeys.X_ULTA_COUNTRY.getValue(),
            requestCountry,
            UltaHeaderKeys.X_ULTA_LOCALE.getValue(),
            UltaCommonConstants.DEFAULT_COUNTRY_LOCALE);
      }
    }

    if (StringUtils.isNotBlank(ultaHeaders.getChannel())) {
      channel = ultaHeaders.getChannel();
    }

    if (StringUtils.isNotBlank(ultaHeaders.getDevice())) {
      device = ultaHeaders.getDevice();
    }

    if (StringUtils.isNotBlank(ultaHeaders.getStoreId())) {
      storeId = ultaHeaders.getStoreId();
    }

    if (StringUtils.isNotBlank(ultaHeaders.getAssociateId())) {
      associateId = ultaHeaders.getAssociateId();
    }

    if(StringUtils.isNotBlank(ultaHeaders.getTrueClientIp())) {
      trueClientIp = ultaHeaders.getTrueClientIp();
    }
    if(StringUtils.isNotBlank(ultaHeaders.getDeviceCharacteristics())) {
    	deviceCharacteristics = ultaHeaders.getDeviceCharacteristics();
     }

    if(StringUtils.isNotBlank(ultaHeaders.getCorrelationId())) {
      correlationId = ultaHeaders.getCorrelationId();
    }

    return UltaRequestContext.builder()
        .country(requestCountry)
        .locale(locale)
        .deliveryMethod(deliveryMethod)
        .channel(channel)
        .device(device)
        .storeId(storeId)
        .associateId(associateId)
        .trueClientIp(trueClientIp)
        .deviceCharacteristics(deviceCharacteristics)
        .correlationId(correlationId)
        .build();
  }
}