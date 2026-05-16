/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.webclient;

import com.ulta.dsp.common.model.UltaWebClientBuilderType;
import org.springframework.web.reactive.function.client.WebClient;

public interface UltaWebClientBuilderFactory {

  public WebClient.Builder getWebClientBuilder(UltaWebClientBuilderType webClientType);
}