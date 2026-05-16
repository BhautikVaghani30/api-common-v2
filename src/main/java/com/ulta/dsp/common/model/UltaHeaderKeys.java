/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import lombok.Getter;

public enum UltaHeaderKeys {
  X_ULTA_USER_ID("X-ULTA-USER-ID"),
  X_ULTA_AUTH0_ID("X-ULTA-AUTH0-ID"),
  X_ULTA_CT_ID("X-ULTA-CT-ID"),
  X_ULTA_SESSION_ID("X-ULTA-SESSION-ID"),
  X_ULTA_AUTH_STATUS("X-ULTA-AUTH-STATUS"),
  X_ULTA_COUNTRY("X-ULTA-COUNTRY"),
  X_ULTA_LOCALE("X-ULTA-LOCALE"),
  X_ULTA_ASSOCIATE_ID("X-ULTA-ASSOCIATE-ID"),
  X_ULTA_STORE_ID("X-ULTA-STORE-ID"),
  X_ULTA_CHANNEL("X-ULTA-CHANNEL"),
  X_ULTA_DEVICE("X-ULTA-DEVICE"),
  X_AKAMAI_DEVICE_CHARACTERISTICS("X-AKAMAI-DEVICE-CHARACTERISTICS"),
  X_ULTA_TRUE_CLIENT_IP("true-client-ip"),
  TRACE_PARENT("Traceparent"),
  TRACE_STATE("Tracestate"),
  X_ULTA_LOYALTY_MEMBER_ID("X-ULTA-LOYALTY-MEMBER-ID"),
  X_ULTA_LOYALTY_MEMBER_STATUS("X-ULTA-LOYALTY-MEMBER-STATUS"),
  X_ULTA_CORRELATION_ID("X-ULTA-CORRELATION-ID");

  UltaHeaderKeys(String value) {
    this.value = value;
  }

  @Getter private String value;
}