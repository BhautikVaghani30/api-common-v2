/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.validation;

import com.ulta.dsp.common.config.UltaAddressRegexConfig;
import com.ulta.dsp.common.domain.data.UltaField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class UltaAddressFieldValidator
    implements ConstraintValidator<UltaValidAddressField, UltaField> {
  private static final Pattern regex = Pattern.compile(".*");
  private Pattern pattern;
  @Autowired private UltaAddressRegexConfig addressRegexConfig;

  public Pattern getPattern() {
    return pattern;
  }

  public void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  @Override
  public void initialize(UltaValidAddressField constraintAnnotation) {
    if (!StringUtils.isEmpty(constraintAnnotation.regex())
        && addressRegexConfig.getRegexPattern().containsKey(constraintAnnotation.regex())) {
      String regexValue = addressRegexConfig.getRegexPattern().get(constraintAnnotation.regex());
      setPattern(Pattern.compile(regexValue));
    } else {
      setPattern(regex);
    }
  }

  @Override
  public boolean isValid(UltaField value, ConstraintValidatorContext context) {
    return (null != value
        && !StringUtils.isEmpty(value.getValue())
        && getPattern().matcher(value.getValue().toString()).matches());
  }
}