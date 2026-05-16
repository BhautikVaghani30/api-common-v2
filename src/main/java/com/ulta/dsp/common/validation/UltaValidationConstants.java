/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.validation;

public final class UltaValidationConstants {

  public static final String PATTERN_EMAIL_VALID =
      "(?=^.{3,40}$)((?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\]))";
  public static final String PATTERN_MEMBER_ID_VALID = "^(\\s*|\\d{1,100})$";
  public static final String PATTERN_PASS_VALID =
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&_])(?!.*[\\s\\[\\]<>{}]).{8,40}$";
  public static final String PATTERN_CHANGE_PASS =
      "^(\\s*|(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$^+=!*()@%&_])(?!.*[\\s\\[\\]<>{}]).{8,40})$";
  public static final String PATTERN_DIGITAL_ID = "^[a-zA-Z0-9-]{1,40}$";
  public static final String ENTITY_NUMERIC_PATTERN = "^[0-9]+$";
  public static final String PATTERN_PHONE_VALID = "^(\\(\\d{3}\\))\\s(\\d{3})-(\\d{4})$";
  public static final String NAME_REGEX = "^[a-zA-Z0-9\\?\\-\\'\\.\\%\\/\\@\\&\\(\\)\\s]{1,255}$";
  public static final String CITY_REGEX = "^[\\pL\\pN\\pZs]{1,30}$";
  public static final String ZIP_CODE_REGEX =
      "^$|^[0-9]{5}$|^[a-zA-Z][0-9][a-zA-Z] ?[0-9][a-zA-Z][0-9]$";
  public static final String ZIP_CODE_REGEX_WITHOUT_EMPTY_STRING =
      "^[0-9]{5}$|^[a-zA-Z][0-9][a-zA-Z] ?[0-9][a-zA-Z][0-9]$";
  public static final String ADDRESS_LINE_REGEX = "^[\\pL\\pN\\pZs]{1,200}$";
  public static final String PHONE_NUMBER_REGEX = "^$|^[0-9]{3}-[0-9]{3}-[0-9]{4}$";
  public static final String PHONE_NUMBER_REGEX_WITHOUT_EMPTY_STRING =
      "^[0-9]{3}-[0-9]{3}-[0-9]{4}$";
  public static final String COUNTRY_STATE_FIELD_REGEX = "^[a-zA-Z0-9]+$";

  public static final String ALPHANUMERIC = "ALPHANUMERIC";
  public static final String POSTALCODE = "POSTALCODE";
  public static final String PHONENO = "PHONENO";
  public static final String EMAIL = "EMAIL";
  public static final String PROVINCIAL = "PROVINCIAL";
  public static final String DEFAULT = ".*";

  private UltaValidationConstants() {}
}