/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import com.ulta.dsp.common.constants.UltaWebClientBuilderTypeConstants;
import lombok.Getter;

public enum UltaWebClientBuilderType {
  CORE(UltaWebClientBuilderTypeConstants.CORE_WEB_CLIENT_BUILDER),
  ORCH(UltaWebClientBuilderTypeConstants.ORCH_WEB_CLIENT_BUILDER),
  ORCH_WITH_COMPRESSION(UltaWebClientBuilderTypeConstants.ORCH_WITH_COMPRESSION_WEB_CLIENT_BUILDER),
  EXTERNAL(UltaWebClientBuilderTypeConstants.EXTERNAL_WEB_CLIENT_BUILDER);

  @Getter private final String webClientBuilderName;

  UltaWebClientBuilderType(String webClientBuilderName) {
    this.webClientBuilderName = webClientBuilderName;
  }

  @Override
  public String toString() {
    return webClientBuilderName;
  }
}