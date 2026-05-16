/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.buy.checkout.i18n;

import jakarta.annotation.PostConstruct;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

/** Static initializer class to load resource bundle messageSource */
@Component
public class UltaStaticContextInitializer {
  private ResourceBundleMessageSource messageSource;

  public UltaStaticContextInitializer(ResourceBundleMessageSource pMessageSource) {
    messageSource = pMessageSource;
  }

  @PostConstruct
  public void init() {
    UltaResourceManager.setMessageSource(messageSource);
  }
}