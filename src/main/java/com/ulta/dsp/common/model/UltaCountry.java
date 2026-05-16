/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import lombok.Getter;

public enum UltaCountry {
  US("US"),
  CA("CA"),
  DEFAULT("US");

  UltaCountry(String value) {
    this.value = value;
  }

  @Getter private String value;

  @Override
  public String toString() {
    return this.value;
  }
}