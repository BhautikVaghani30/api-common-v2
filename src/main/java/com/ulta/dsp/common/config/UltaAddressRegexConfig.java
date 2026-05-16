/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/** Config class to hold configuration to connect with commerce tools */
@Configuration
public class UltaAddressRegexConfig {

  @Value("#{${address.validation.regex:null}}")
  private Map<String, String> regexPattern;

  public Map<String, String> getRegexPattern() {
    return regexPattern;
  }

}