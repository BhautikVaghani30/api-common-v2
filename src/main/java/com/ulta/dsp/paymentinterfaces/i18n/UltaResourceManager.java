/*
 * Copyright (C) 2022 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.paymentinterfaces.i18n;

import java.util.Objects;
import java.util.regex.Pattern;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import lombok.extern.slf4j.Slf4j;

/**
 * Class provides us capability to support internalization messages by reading them from locale
 * specific resource bundles
 */
@Slf4j
public final class UltaResourceManager {
  @Setter private static ResourceBundleMessageSource messageSource;

  private static final int INDEX_0 = 0;
  private static final int INDEX_1 = 1;
  private static final int CHK_LEN = 2;
  private static final Pattern DELIMITER = Pattern.compile("\\|");

  private UltaResourceManager() {}

  /**
   * Method is used to retrieve a message for the given key from the resource bundle for the current
   * locale.
   *
   * <p>If the messageSource is not initialized, the msgKey is empty, or the key is not found in the
   * bundle, it returns the original msgKey as a fallback.
   *
   * @param msgKey The key to look up in the resource bundle.
   * @return The resolved message or the original msgKey if resolution fails.
   */
  public static String getMessageKey(String msgKey) {
    try {
      if (Objects.isNull(messageSource) || StringUtils.isEmpty(msgKey)) {
        return msgKey;
      }
      return messageSource.getMessage(msgKey, null, LocaleContextHolder.getLocale());
    } catch (NoSuchMessageException noSuchMessageException) {
      log.error(
          "UltaResourceManager : getMessageKey :: msgKey : {}, Error Message : {}, Exception : ",
          msgKey,
          noSuchMessageException.getMessage(),
          noSuchMessageException);
      return msgKey;
    }
  }

  /**
   * Method is used to parse a resource bundle string to extract the HTTP status code.
   *
   * <p>The input string is expected to be pipe-delimited (e.g., "404|NOT_FOUND"). This method
   * extracts the first part of the string and converts it to an integer.
   *
   * @param key The resource bundle value containing the HTTP code.
   * @return The extracted HTTP status code as an integer, or 0 if not found or invalid.
   */
  public static int getHttpCode(String key) {
    String httpStatus = getValue(key, INDEX_0);
    if (StringUtils.isEmpty(httpStatus)) {
      log.warn("UltaResourceManager : getHttpCode :: httpStatus is not defined for key : {}", key);
      return 0;
    }
    return Integer.parseInt(httpStatus);
  }

  /**
   * Method is used to parse a resource bundle string to extract the custom error code.
   *
   * <p>The input string is expected to be pipe-delimited (e.g., "404|ERROR_CODE_123"). This method
   * extracts the second part of the string. If there is no delimiter, the original key is returned.
   *
   * @param key The resource bundle value containing the custom error code.
   * @return The extracted custom error code string, or null if it's not defined.
   */
  public static String getCode(String key) {
    String messageCode = key;
    if (key.split(DELIMITER.pattern()).length > 1) {
      messageCode = getValue(key, INDEX_1);
    }
    if (StringUtils.isEmpty(messageCode)) {
      log.warn("UltaResourceManager : getCode :: message code is not defined for key : {}", key);
      return null;
    }
    return messageCode;
  }

  /**
   * Method is used to split a pipe-delimited string and retrieves the value at a specific index.
   *
   * <p>This method expects the input string {@code key} to contain at least two parts separated by
   * a pipe ('|'), as defined by the {@code CHK_LEN} constant. If the key is null, empty, or does
   * not contain at least two parts, this method will return {@code null}.
   *
   * @param key The pipe-delimited string to parse.
   * @param index The zero-based index of the value to retrieve.
   * @return The string value at the specified index, or {@code null} if the key is invalid or
   *     doesn't have enough parts.
   */
  private static String getValue(String key, int index) {
    if (StringUtils.isEmpty(key)) {
      log.warn("UltaResourceManager : getValue :: key is null or empty");
      return null;
    }
    String[] values = DELIMITER.split(key);
    if (values.length >= CHK_LEN) {
      return values[index];
    }
    return null;
  }
}