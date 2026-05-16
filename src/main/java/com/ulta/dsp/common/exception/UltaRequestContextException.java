/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UltaRequestContextException extends UltaException {

  private static final long serialVersionUID = 1L;

  public UltaRequestContextException(String exception) {
    super(exception);
  }
}