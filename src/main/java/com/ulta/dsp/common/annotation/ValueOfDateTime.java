/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.annotation;

import com.ulta.dsp.common.validation.DateTimeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * DateTime format annotation
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {DateTimeValidator.class})
public @interface ValueOfDateTime {
  /** Date format to be validated */
  String datePattern() default "yyyy-MM-dd";

  /** Validation failure message */
  String message() default "{validation.date.format.invalid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  boolean ignoreCase() default false;
}