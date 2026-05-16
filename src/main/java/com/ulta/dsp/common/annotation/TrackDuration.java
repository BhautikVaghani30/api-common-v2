/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrackDuration { // Optional custom metric name
  String value() default "";
  Tag[] tags() default {};
}