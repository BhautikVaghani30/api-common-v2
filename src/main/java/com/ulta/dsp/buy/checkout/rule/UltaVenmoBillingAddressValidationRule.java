/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.buy.checkout.rule;

import com.ulta.dsp.buy.checkout.constants.UltaCheckoutConstants;
import com.ulta.dsp.buy.checkout.i18n.UltaResourceManager;
import com.ulta.dsp.buy.checkout.model.orch.response.UltaAddressValidationResponse;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.data.UltaField;
import com.ulta.dsp.common.model.UltaMessage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.drools.core.util.StringUtils;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class UltaVenmoBillingAddressValidationRule {

  private static final Logger LOG =
      LoggerFactory.getLogger(UltaVenmoBillingAddressValidationRule.class);

  private static final String US_ZIP_CODE_REGEX = "US_ZIP_CODE";
  private static final String CITY_REGEEX = "CITY";
  private static final String NAME_REGEX = "NAME";
  private static final String PHONE_NUMBER_REGEX = "PHONENO";
  private static final String PATTERN_EMAIL_VALID = "EMAIL";
  private static final String ADDRESS_LINE_REGEX = "ADDRESS";
  private static final String COUNTRY_STATE_FIELD_REGEX = "STATE";

  @Autowired
  @Qualifier("shippingAddressRule")
  private KieContainer kContainer;

  @Value("#{${address.validation.regex}}")
  private Map<String, String> regexPattern;

  public UltaAddressValidationResponse validateShippingAddress(
      UltaField ultaAddressField, String fieldName) {

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
      kieSession.setGlobal("stateRegex", regexPattern.get(COUNTRY_STATE_FIELD_REGEX));
      kieSession.setGlobal("zipCodeRegex", regexPattern.get(US_ZIP_CODE_REGEX));

      kieSession.fireAllRules();
    } finally {
      if (kieSession != null) {
        kieSession.dispose();
      }
    }
    return ultaAddressValidationResponse;
  }

  /**
   * @param venmoBillingAddress Venmo billing address
   * @return ultaAddressField
   */
  public List<UltaField> validate(UltaAddress venmoBillingAddress) {
    if (Objects.isNull(venmoBillingAddress)) {
      return Collections.emptyList();
    }
    Field[] addressFields = UltaAddress.class.getDeclaredFields();
    return Arrays.stream(addressFields)
        .filter(
            addField ->
                UltaField.class == addField.getType()
                    && !UltaCheckoutConstants.ADDITIONAL_ADDRESS_INFO.equalsIgnoreCase(
                        addField.getName())
                    && !UltaCheckoutConstants.DISPLAY_NAME.equalsIgnoreCase(addField.getName())
                    && !UltaCheckoutConstants.LATITUDE.equalsIgnoreCase(addField.getName())
                    && !UltaCheckoutConstants.LONGITUDE.equalsIgnoreCase(addField.getName())
                    && !UltaCheckoutConstants.FIRST_NAME.equalsIgnoreCase(addField.getName())
                    && !UltaCheckoutConstants.LAST_NAME.equalsIgnoreCase(addField.getName())
                    && !UltaCheckoutConstants.EMAIL.equalsIgnoreCase(addField.getName())
                    && !UltaCheckoutConstants.PHONE_NUM.equalsIgnoreCase(addField.getName()))
        .map(
            addressfield -> {
              ReflectionUtils.makeAccessible(addressfield);
              UltaField ultaAddressField = null;
              if (addressfield.getName().equalsIgnoreCase(UltaCheckoutConstants.PHONE_NUM)) {
                ultaAddressField =
                    (UltaField) ReflectionUtils.getField(addressfield, venmoBillingAddress);
                if (ultaAddressField.getValue() != null
                    && ultaAddressField.getValue().contains(UltaCheckoutConstants.DASH)) {
                  ultaAddressField.setValue(
                      ultaAddressField.getValue().replace(UltaCheckoutConstants.DASH, StringUtils.EMPTY));
                }
              } else {
                ultaAddressField =
                    (UltaField) ReflectionUtils.getField(addressfield, venmoBillingAddress);
              }
              UltaAddressValidationResponse ultaAddressValidationResponse =
                  validateShippingAddress(ultaAddressField, addressfield.getName());

              if (!ultaAddressValidationResponse.isValid()) {
                ultaAddressField.setMessages(
                    new ArrayList<>(
                        Arrays.asList(
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