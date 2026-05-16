/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import lombok.Getter;

public enum UltaQueryParamKeys {
  X_ULTA_COUNTRY("country"),
  X_ULTA_LOCALE("locale"),
  X_ULTA_DELIVERY_METHOD("deliveryMethod"),
  X_ULTA_CHANNEL("channel"),
  X_ULTA_ASSOCIATE_ID("associateId"),
  X_ULTA_STORE_ID("storeId"),
  X_ULTA_DEVICE("device"),
  X_ULTA_TRUE_CLIENT_IP("trueClientIp"),
  X_AKAMAI_DEVICE_CHARACTERISTICS("deviceCharacteristics"),
  X_ULTA_CORRELATION_ID("correlationId");

  UltaQueryParamKeys(String value) {
    this.value = value;
  }

  @Getter private String value;
}