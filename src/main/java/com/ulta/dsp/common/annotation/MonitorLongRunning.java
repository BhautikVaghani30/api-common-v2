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
 * An annotation to monitor the duration of a blocking method, recording its execution time and
 * incrementing a counter if it exceeds a specified threshold.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MonitorLongRunning {
  /** The execution time threshold after which the method is considered "long-running". */
  String threshold();

  /** The time unit for the threshold. Defaults to MILLISECONDS. */
  TimeUnit unit() default TimeUnit.MILLISECONDS;
  Tag[] tags() default {};
}