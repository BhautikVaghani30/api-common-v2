/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.webclient;

import com.ulta.dsp.common.model.UltaWebClientBuilderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UltaWebClientBuilderService {

  private UltaWebClientBuilderFactory ultaWebClientBuilderFactory;
  private UltaWebClientBuilderConfiguration ultaWebClientBuilderConfiguration;

  @Autowired
  public UltaWebClientBuilderService(
      UltaWebClientBuilderFactory ultaWebClientBuilderFactory,
      UltaWebClientBuilderConfiguration ultaWebClientBuilderConfiguration) {
    this.ultaWebClientBuilderFactory = ultaWebClientBuilderFactory;
    this.ultaWebClientBuilderConfiguration = ultaWebClientBuilderConfiguration;
  }

  public WebClient.Builder create(UltaWebClientBuilderType webClientType) {
    return ultaWebClientBuilderFactory.getWebClientBuilder(webClientType);
  }

  public WebClient.Builder createCoreWebClient(String webClientConfigName) {
    return ultaWebClientBuilderConfiguration.createCoreBuilder( webClientConfigName);
  }

  public WebClient.Builder createOrchWebClient(String webClientConfigName) {
    return ultaWebClientBuilderConfiguration.createOrchestrationBuilder( webClientConfigName);
  }

  public WebClient.Builder createExternalWebClient(String webClientConfigName) {
    return ultaWebClientBuilderConfiguration.createExternalBuilder( webClientConfigName);
  }

  public WebClient.Builder createOrchWebClientWithCompression(String webClientConfigName) {
    return ultaWebClientBuilderConfiguration.createOrchestrationWithCompressionBuilder(
        webClientConfigName);
  }
}