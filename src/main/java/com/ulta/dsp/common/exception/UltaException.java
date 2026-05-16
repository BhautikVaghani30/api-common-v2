/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.exception;

public class UltaException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * This is used for setting the message for an UltaException
   *
   * @param exception Ulta wrapped exception
   */
  public UltaException(String exception) {
    super(exception);
  }
}