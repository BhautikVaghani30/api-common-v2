/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.validation;

import com.ulta.dsp.common.annotation.ValueOfDateTime;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * This is an implementor class for {@link com.ulta.dsp.common.annotation.ValueOfDateTime}
 * annotation interface.
 */
@Slf4j
public class DateTimeValidator implements ConstraintValidator<ValueOfDateTime, String> {

  private static final String PRE_TEXT_DATE = "--";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    boolean isValid = true;
    if (Optional.ofNullable(value).isPresent()) {
      try {
        MonthDay md = MonthDay.parse(PRE_TEXT_DATE + value);
        log.debug("Successfully validated day {} & month {}", md.getDayOfMonth(), md.getMonth());
      } catch (DateTimeParseException e) {
        log.error("Unable to parse date in request : {}", value);
        isValid = false;
      }
    }
    return isValid;
  }
}