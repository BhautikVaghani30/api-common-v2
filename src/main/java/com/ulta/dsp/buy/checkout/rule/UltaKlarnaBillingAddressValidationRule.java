/*
 * Copyright (C) 2026 ULTA
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
import org.apache.commons.lang3.StringUtils;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Performs validation on Klarna billing address fields using a Drools rule engine.
 *
 * <p>This class iterates through the fields of an {@link UltaAddress} object, applying a set of
 * validation rules defined in a Kie (Drools) container. If a field fails validation, an error
 * message is attached directly to that field.
 */
@Component
public class UltaKlarnaBillingAddressValidationRule {

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

  /**
   * Validates a single address field against a set of Drools rules.
   *
   * <p>This method inserts a single {@link UltaField} into a new Kie (Drools) session and fires all
   * rules. The rules use the provided regex patterns to validate the field's value.
   *
   * @param ultaAddressField The address field to be validated.
   * @param fieldName The name of the field, used by the rules to apply the correct regex.
   * @return An {@link UltaAddressValidationResponse} containing the result of the validation,
   *     including an error message code if validation fails.
   */
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
   * Validates the fields of a Klarna billing address.
   *
   * <p>This method iterates through the fields of the provided {@link UltaAddress} using
   * reflection, skipping non-address fields (like name, email, and phone). Each relevant field is
   * passed to the Drools rule engine for validation. If a field is invalid, an error message is
   * added to its internal message list.
   *
   * @param klarnaBillingAddress The Klarna billing address to validate.
   * @return A list of {@link UltaField} objects from the input address, with error messages
   *     attached to any that failed validation. Returns an empty list if the input address is null.
   */
  public List<UltaField> validate(UltaAddress klarnaBillingAddress) {
    if (Objects.isNull(klarnaBillingAddress)) {
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
                    && !UltaCheckoutConstants.LONGITUDE.equalsIgnoreCase(addField.getName()))
        .map(
            addressfield -> {
              ReflectionUtils.makeAccessible(addressfield);
              UltaField ultaAddressField =
                  (UltaField) ReflectionUtils.getField(addressfield, klarnaBillingAddress);

              if (addressfield.getName().equalsIgnoreCase(UltaCheckoutConstants.PHONE_NUM)) {
                if (Objects.nonNull(ultaAddressField)
                    && StringUtils.isNotBlank(ultaAddressField.getValue())
                    && ultaAddressField.getValue().contains(UltaCheckoutConstants.DASH)) {
                  ultaAddressField.setValue(
                      ultaAddressField
                          .getValue()
                          .replace(UltaCheckoutConstants.DASH, StringUtils.EMPTY));
                }
              }
              UltaAddressValidationResponse ultaAddressValidationResponse =
                  validateShippingAddress(ultaAddressField, addressfield.getName());

              /* Only set messages if the field is invalid AND the field object itself exists. */
              if (!ultaAddressValidationResponse.isValid() && Objects.nonNull(ultaAddressField)) {
                ultaAddressField.setMessages(
                    new ArrayList<>(
                        Collections.singletonList(
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