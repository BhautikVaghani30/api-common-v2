/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.buy.checkout.i18n;

import com.ulta.dsp.common.model.UltaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Class provides us capability to support internalization messages by reading them from locale
 * specific resource bundles
 */
public final class UltaResourceManager {
  private static final Logger LOG = LoggerFactory.getLogger(UltaResourceManager.class);

  private static final int CHK_LEN = 2;
  private static final int INDEX_0 = 0;
  private static final int INDEX_1 = 1;
  private static final Pattern DELIMITER = Pattern.compile("\\|");
  private static ResourceBundleMessageSource messageSource;

  private UltaResourceManager() {}

  public static void setMessageSource(ResourceBundleMessageSource pMessageSource) {
    messageSource = pMessageSource;
  }

  /**
   * Method that returns locale specific message based on the provided message key
   *
   * @param msgKey
   * @return message
   */
  public static String getMessageKey(String msgKey) {
    try {
      if (null == messageSource || StringUtils.isEmpty(msgKey)) {
        return msgKey;
      }
      return messageSource.getMessage(msgKey, null, LocaleContextHolder.getLocale());
    } catch (NoSuchMessageException e) {
      LOG.error("getMessageKey, msgKey : {}, error : {}", msgKey, e.getMessage());
      return msgKey;
    }
  }

  /**
   * Method that returns http status code by splitting message string by | delimiter. message looks
   * like - 200|CoreCart-CartNotFound-1004|Cart does not exist in the system. this method would
   * return 200
   *
   * @param key
   * @return httpStatusCode
   */
  public static int getHttpCode(String key) {
    String httpStatus = getValue(key, INDEX_0);
    if (StringUtils.isEmpty(httpStatus)) {
      LOG.warn("httpStatus :: httpStatus is not defined for key :: {}", key);
      return 0;
    }
    return Integer.parseInt(httpStatus);
  }

  /**
   * Method that returns error/message code by splitting message string by | delimiter. message
   * looks like - 200|CoreCart-CartNotFound-1004|Cart does not exist in the system. this method
   * would return CoreCart-CartNotFound-1004
   *
   * @param key
   * @return httpStatusCode
   */
  public static String getCode(String key) {
    String messageCode = getValue(key, INDEX_1);
    if (StringUtils.isEmpty(messageCode)) {
      LOG.warn("getMessageCode :: message  code is not defined for key :: {}", key);
      return null;
    }
    return messageCode;
  }

  /**
   * Private helper method for split message key by delimiter
   *
   * @param key
   * @param index
   * @return
   */
  private static String getValue(String key, Integer index) {
    if (StringUtils.isEmpty(key)) {
      LOG.warn("getValue :: key is null ");
      return null;
    }
    var values = key.split(DELIMITER.pattern());
    if (values.length >= CHK_LEN) {
      return values[index];
    }
    return null;
  }

  /**
   * helper method for populating message code
   *
   * @param message
   * @return
   */
  public static UltaMessage prepareValidationMessageCode(String message) {
    UltaMessage accountErrorMessage;
    String code = messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
    accountErrorMessage = UltaMessage.builder().type("error").code(code).build();
    return accountErrorMessage;
  }

}