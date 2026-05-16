/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.validation;

import com.ulta.dsp.common.annotation.ValueOfDate;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/** This is an implementor class for {@link ValueOfDate} annotation interface. */
@Slf4j
public class DateValidator implements ConstraintValidator<ValueOfDate, String> {

  private DateTimeFormatter dateFormatter;

  @Override
  public void initialize(ValueOfDate annotation) {

    dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;
    dateFormatter = DateTimeFormatter.ofPattern(annotation.datePattern());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    boolean isValid = true;
    if (Optional.ofNullable(value).isPresent()) {
      try {

        if (LocalDate.parse(value, this.dateFormatter).isAfter(LocalDate.now())) {
          isValid = false;
        }
      } catch (DateTimeParseException e) {
        log.error("Unable to parse date in request : {}", value);
        isValid = false;
      }
    }
    return isValid;
  }
}