/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UltaHeaderValues {
  private String country;
  private String languageTag;
  private String userId;
  private String auth0Id;
  private String ctId;
  private String sessionId;
  private String authStatus;
  private String deliveryMethod;
  private String channel;
  private String device;
  private String storeId;
  private String associateId;
  private String deviceCharacteristics;
  private String loyaltyMemberId;
  private String loyaltyMemberStatus;

  private String trueClientIp;
  private String correlationId;
}