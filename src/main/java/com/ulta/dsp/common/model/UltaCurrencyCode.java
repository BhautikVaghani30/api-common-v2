/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import java.util.HashMap;
import java.util.Map;

public enum UltaCurrencyCode {
  CA("CAD"),
  US("USD");

  public final String value;
  private static final Map<String, UltaCurrencyCode> CURRENCY_MAP = new HashMap<>();

  UltaCurrencyCode(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  static {
    for (UltaCurrencyCode c : values()) {
      CURRENCY_MAP.put(c.value, c);
    }
  }
}