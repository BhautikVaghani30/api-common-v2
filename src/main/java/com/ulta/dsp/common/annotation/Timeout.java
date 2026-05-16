/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * An annotation to enforce a timeout on a reactive method (Mono/Flux) and increment a counter
 * metric if a timeout occurs.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {
  /** The duration of the timeout. */
  String value();

  /** The time unit for the duration. Defaults to MILLISECONDS. */
  TimeUnit unit() default TimeUnit.MILLISECONDS;
  Tag[] tags() default {};
}