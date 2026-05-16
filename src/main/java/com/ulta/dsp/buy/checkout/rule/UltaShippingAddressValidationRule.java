/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.buy.checkout.rule;

import com.ulta.dsp.buy.checkout.constants.UltaCheckoutConstants;
import com.ulta.dsp.buy.checkout.constants.UltaURIConstants;
import com.ulta.dsp.buy.checkout.i18n.UltaResourceManager;
import com.ulta.dsp.buy.checkout.model.orch.response.UltaAddressValidationResponse;
import com.ulta.dsp.buy.checkout.util.UltaCheckoutUtils;
import com.ulta.dsp.common.context.UltaContext;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.data.UltaField;
import com.ulta.dsp.common.model.UltaMessage;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UltaShippingAddressValidationRule {

  private static final Logger LOG =
      LoggerFactory.getLogger(UltaShippingAddressValidationRule.class);

  private static final String US_ZIP_CODE_REGEX = "US_ZIP_CODE";
  private static final String CITY_REGEEX = "CITY";
  private static final String NAME_REGEX = "NAME";
  private static final String PHONE_NUMBER_REGEX = "PHONENO";
  private static final String PATTERN_EMAIL_VALID = "EMAIL";
  private static final String ADDRESS_LINE_REGEX = "ADDRESS";
  private static final String ZIP_CODE_REGEX = "POSTALCODE";
  private static final String COUNTRY_STATE_FIELD_REGEX = "STATE";
  private static final String COUNTRY_PROVINCE_FIELD_REGEX = "PROVINCIAL";

  @Autowired
  @Qualifier("shippingAddressRule")
  private KieContainer kContainer;

  @Value("#{${address.validation.regex}}")
  private Map<String, String> regexPattern;

  public UltaAddressValidationResponse validateShippingAddress(
      UltaField ultaAddressField, String fieldName, String addressType) {

    UltaAddressValidationResponse ultaAddressValidationResponse =
        new UltaAddressValidationResponse();
    ultaAddressValidationResponse.setValid(true);
    KieSession kieSession = null;
    try {
      kieSession = kContainer.newKieSession();
      kieSession.insert(ultaAddressValidationResponse);
      kieSession.insert(ultaAddressField);
      kieSession.setGlobal("fieldName", fieldName);
      kieSession.setGlobal("cityRegex", regexPattern.get(CITY_REGEEX));
      kieSession.setGlobal("nameRegex", regexPattern.get(NAME_REGEX));
      kieSession.setGlobal("phoneRegex", regexPattern.get(PHONE_NUMBER_REGEX));
      kieSession.setGlobal("emailRegex", regexPattern.get(PATTERN_EMAIL_VALID));
      kieSession.setGlobal("addressRegex", regexPattern.get(ADDRESS_LINE_REGEX));

      if (UltaCheckoutUtils.isUSAFlow(UltaContext.getContext())) {
        kieSession.setGlobal("stateRegex", regexPattern.get(COUNTRY_STATE_FIELD_REGEX));
        kieSession.setGlobal("zipCodeRegex", regexPattern.get(US_ZIP_CODE_REGEX));
      } else {
        kieSession.setGlobal("zipCodeRegex", regexPattern.get(ZIP_CODE_REGEX));
        kieSession.setGlobal("stateRegex", regexPattern.get(COUNTRY_PROVINCE_FIELD_REGEX));
      }
      kieSession.fireAllRules();
    } finally {
      if (kieSession != null) {
        kieSession.dispose();
      }
    }
    return ultaAddressValidationResponse;
  }

  /**
   * @param ultaShippingAddress ulta shipping address
   * @param errorCode error code
   * @return ultaAddressField
   */
  public List<UltaField> validate(
      UltaAddress ultaShippingAddress, String errorCode, String addressType, String checkoutType) {
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
                        .equalsIgnoreCase(UltaCheckoutConstants.ADDITIONAL_ADDRESS_INFO)
                    && !addField.getName().equalsIgnoreCase(UltaCheckoutConstants.DISPLAY_NAME)
                    && !addField.getName().equalsIgnoreCase(UltaCheckoutConstants.LATITUDE)
                    && !addField.getName().equalsIgnoreCase(UltaCheckoutConstants.LONGITUDE))
        .map(
            addressfield -> {
              ReflectionUtils.makeAccessible(addressfield);
              LOG.debug("validate  :: addressField, Id : {}", addressfield.getName());
              UltaField ultaAddressField = null;
              if (addressfield.getName().equalsIgnoreCase(UltaCheckoutConstants.PHONE_NUM)) {
                ultaAddressField =
                    (UltaField) ReflectionUtils.getField(addressfield, ultaShippingAddress);
                if (ultaAddressField.getValue() != null
                    && ultaAddressField.getValue().contains(UltaCheckoutConstants.DASH)) {
                  ultaAddressField.setValue(
                      ultaAddressField
                          .getValue()
                          .replace(UltaCheckoutConstants.DASH, UltaCheckoutConstants.BLANK));
                }
              } else {
                ultaAddressField =
                    (UltaField) ReflectionUtils.getField(addressfield, ultaShippingAddress);
              }

              if (UltaURIConstants.EXPRESS_APPLE_PAY.equalsIgnoreCase(checkoutType)) {
                if (addressfield.getName().equalsIgnoreCase(UltaCheckoutConstants.FIRST_NAME)
                    || addressfield.getName().equalsIgnoreCase(UltaCheckoutConstants.LAST_NAME)
                    || addressfield.getName().equalsIgnoreCase(UltaCheckoutConstants.PHONE_NUM)
                    || addressfield.getName().equalsIgnoreCase(UltaCheckoutConstants.ADDRESS_1)
                    || addressfield.getName().equalsIgnoreCase(UltaCheckoutConstants.ADDRESS_2)
                    || addressfield.getName().equalsIgnoreCase(UltaCheckoutConstants.EMAIL)) {
                  return ultaAddressField;
                }
              }

              UltaAddressValidationResponse ultaAddressValidationResponse =
                  validateShippingAddress(ultaAddressField, addressfield.getName(), addressType);

              if (!ultaAddressValidationResponse.isValid()) {
                ultaAddressField.setMessages(
                        new ArrayList<>(Collections.singletonList(
                                UltaMessage.builder()
                                        .type(UltaMessage.UltaMessageType.ERROR.getValue())
                                        .code(
                                                UltaResourceManager.getMessageKey(
                                                        ultaAddressValidationResponse.getMessage().getCode()))
                                        .build())));
              }
              return ultaAddressField;
            })
        .collect(Collectors.toList());
  }
}