/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@ToString
@Builder
public class aWDF3TUltaRequestContext {

  private UltaCountry country;
  private Locale locale;
  private String userId;
  private String sessionId;
  private String authStatus;
  private String channel;
  private String device;
  private String storeId;
  private String deliveryMethod;
  private String associateId;
  private String deviceCharacteristics;
  private String loyaltyMemberId;
  private String loyaltyMemberStatus;

  private String trueClientIp;
  @Setter private String correlationId;

  @Builder.Default
  private Map<String, Object> additionalProperties = new ConcurrentHashMap<>();
}