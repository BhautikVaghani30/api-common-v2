/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.buy.checkout.util;

import com.ulta.dsp.buy.checkout.constants.UltaCheckoutConstants;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.data.UltaField;
import com.ulta.dsp.common.model.UltaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class AddressValidator {

  private static final Logger LOG = LoggerFactory.getLogger(AddressValidator.class);

  private AddressValidator() {}

  /**
   * @param ultaShippingAddress
   * @param validator
   * @return
   */
  public static List<UltaField> validate(
      UltaAddress ultaShippingAddress, Validator validator, String errorCode) {
    if (null == ultaShippingAddress) {
      return Collections.emptyList();
    }
    Field[] addressFields = UltaAddress.class.getDeclaredFields();
    return Arrays.stream(addressFields)
        .filter(
            addField ->
                addField.getType() == UltaField.class
                    && !addField
                        .getName()
                        .equalsIgnoreCase(UltaCheckoutConstants.ADDITIONAL_ADDRESS_INFO))
        .map(
            addressfield -> {
              ReflectionUtils.makeAccessible(addressfield);
              LOG.debug("validate  :: addressField, Id : {}", addressfield.getName());
              UltaField ultaAddressField =
                  (UltaField) ReflectionUtils.getField(addressfield, ultaShippingAddress);
              Set<ConstraintViolation<UltaAddress>> constraintViolations =
                  validator.validateProperty(ultaShippingAddress, addressfield.getName());
              List<UltaMessage> messageList;
              if (!constraintViolations.isEmpty()) {
                messageList = getConstraintErrorMessageList(errorCode, constraintViolations);
                ultaAddressField.setMessages(messageList);
              }
              return ultaAddressField;
            })
        .collect(Collectors.toList());
  }

  /**
   * This method return the list of error messages.
   *
   * @param errorCode
   * @param constraintViolations
   * @return
   */
  private static List<UltaMessage> getConstraintErrorMessageList(
      String errorCode, Set<ConstraintViolation<UltaAddress>> constraintViolations) {
    List<UltaMessage> messageList;
    messageList =
        constraintViolations.stream()
            .map(
                constraintViolation ->
                    UltaMessage.builder()
                        .type(UltaMessage.UltaMessageType.ERROR.getValue())
                        .code(errorCode)
                        .build())
            .collect(Collectors.toList());
    return messageList;
  }
}