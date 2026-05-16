/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.context;

import com.ulta.dsp.common.model.UltaRequestContext;

public final class UltaContext {
  static UltaRequestContextAdapter ultaRequestContextAdapter;

  private static final String ADAPTER_NOT_NULL = "UltaRequestContextAdapter cannot be null.";

  private UltaContext() {}

  static {
    ultaRequestContextAdapter = new UltaRequestContextAdapter();
  }

  public static void clear() {
    if (ultaRequestContextAdapter == null) {
      throw new IllegalStateException(ADAPTER_NOT_NULL);
    }
    ultaRequestContextAdapter.clear();
  }

  public static UltaRequestContext getContext() {
    if (ultaRequestContextAdapter == null) {
      throw new IllegalStateException(ADAPTER_NOT_NULL);
    }
    return ultaRequestContextAdapter.getCurrentContext();
  }

  public static void setContext(UltaRequestContext ultaRequestContext) {
    if (ultaRequestContextAdapter == null) {
      throw new IllegalStateException(ADAPTER_NOT_NULL);
    }
    ultaRequestContextAdapter.setContext(ultaRequestContext);
  }

  public static UltaRequestContextAdapter getContextAdapter() {
    return ultaRequestContextAdapter;
  }
}