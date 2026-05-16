/*
 * Copyright (C) 2026 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.security.internal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to mark a field as containing sensitive information that requires masking
 * during JSON serialization.
 *
 * <p>When a field of any type is marked with this annotation, the {@link
 * UltaSensitiveDataSerializer} will intercept the serialization process. It will convert the
 * field's value to a string representation and apply the specified {@link UltaMaskType} to it.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UltaSensitive {

  /**
   * Specifies the type of masking strategy to apply to the annotated field.
   *
   * @return The {@link UltaMaskType} to be used for masking. Defaults to {@link UltaMaskType#FULL}.
   */
  UltaMaskType type() default UltaMaskType.FULL;
}