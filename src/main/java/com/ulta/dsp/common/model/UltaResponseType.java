/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

public enum UltaResponseType {
  SUCCESS("success"),
  FAIL("fail"),
  PARTIAL("partial");

  UltaResponseType(String value) {
    this.value = value;
  }

  private String value;

  /**
   * Gets the type
   *
   * @return type:String
   */
  public String getValue() {
    return value;
  }

  /**
   * Print the object to string
   *
   * @return Stringified UltaResponseType object
   */
  @Override
  public String toString() {
    return "UltaResponseType{" + "type='" + getValue() + '\'' + '}';
  }
}