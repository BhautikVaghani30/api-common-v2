/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.context;

import com.ulta.dsp.common.model.UltaRequestContext;

public class UltaRequestContextAdapter {

  final ThreadLocal<UltaRequestContext> copyOnThreadLocal = new ThreadLocal<>();

  public UltaRequestContext getCurrentContext() {
    return copyOnThreadLocal.get();
  }

  public void setContext(UltaRequestContext ultaRequestContext) {
    copyOnThreadLocal.set(ultaRequestContext);
  }

  public void clear() {
    copyOnThreadLocal.remove();
  }
}