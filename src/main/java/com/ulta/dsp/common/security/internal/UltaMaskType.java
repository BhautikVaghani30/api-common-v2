/*
 * Copyright (C) 2026 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.security.internal;

/** Defines the available strategies for masking sensitive data. */
public enum UltaMaskType {
  /** Replaces the entire value with mask characters matching the original length. */
  FULL,

  /**
   * Masks the beginning of the value with mask characters, leaving only the last four characters
   * visible. If the value is four characters or less, the entire value is masked. Suitable for
   * account numbers or IDs where the last few digits are important for identification.
   */
  PARTIAL,

  /**
   * Masks the entire value with asterisks matching the original length. Used for CVV codes. This is
   * equivalent to {@link #FULL}.
   */
  CVV,

  /**
   * Masks a Primary Account Number (e.g., a credit card number), showing the first six and last
   * four digits, with the middle portion masked. If the value is too short for a meaningful PAN
   * mask (less than 11 characters), the entire value is masked.
   */
  PAN,

  /**
   * Masks the username portion of an email address, showing only the first character of the
   * username, and leaving the domain visible. If the username is one character or less, the entire
   * username is masked. If the email format is invalid (e.g., no '@' symbol), the entire value is
   * masked.
   */
  EMAIL
}