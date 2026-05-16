/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

public enum UltaUpdatedResourceType {
  PROFILE("profile"),
  CART("cart"),
  ORDER("order"),
  ADDRESS("address"),
  SAVEFORLATER("saveforlater"),
  FAVORITES("favorites");

  private String value;

  /**
   * @param value
   */
  private UltaUpdatedResourceType(String value) {
    this.value = value;
  }


  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }


  /**
   * 
   * @return UltaUpdatedResourceType object
   */
  @Override
  public String toString() {
    return "UltaUpdatedResourceType{" + "type='" + getValue() + '\'' + '}';
  }
}