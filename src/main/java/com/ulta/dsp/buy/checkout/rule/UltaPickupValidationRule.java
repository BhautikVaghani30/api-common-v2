/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.buy.checkout.rule;

import com.ulta.dsp.buy.checkout.constants.UltaCheckoutConstants;
import com.ulta.dsp.buy.checkout.i18n.UltaResourceManager;
import com.ulta.dsp.buy.checkout.model.orch.response.UltaAddressValidationResponse;
import com.ulta.dsp.common.domain.data.UltaContactInfo;
import com.ulta.dsp.common.domain.data.UltaField;
import com.ulta.dsp.common.model.UltaMessage;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UltaPickupValidationRule {

  private static final String NAME_REGEX = "NAME";
  private static final String PHONE_NUMBER_REGEX = "PHONENO";
  private static final String PATTERN_EMAIL_VALID = "EMAIL";

  @Autowired
  @Qualifier("pickupRule")
  private KieContainer kContainer;

  @Value("#{${address.validation.regex}}")
  private Map<String, String> regexPattern;

  public UltaAddressValidationResponse validatePickupDetails(
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
      kieSession.setGlobal("nameRegex", regexPattern.get(NAME_REGEX));
      kieSession.setGlobal("phoneRegex", regexPattern.get(PHONE_NUMBER_REGEX));
      kieSession.setGlobal("emailRegex", regexPattern.get(PATTERN_EMAIL_VALID));

      kieSession.fireAllRules();
    } finally {
      if (kieSession != null) {
        kieSession.dispose();
      }
    }
    return ultaAddressValidationResponse;
  }

  /**
   * @param ultaPickupInfo ulta pickup details
   * @param errorCode      error code
   * @return ultaPickUpField
   */
  public List<UltaField> validate(UltaContactInfo ultaPickupInfo, String errorCode, String deliveryMethod) {
    if (null == ultaPickupInfo) {
      return Collections.emptyList();
    }
    Field[] addressFields = UltaContactInfo.class.getDeclaredFields();

    return Arrays.stream(addressFields)
        .map(
            addressfield -> {
              ReflectionUtils.makeAccessible(addressfield);
              log.debug("validate  :: addressField, Id : {}", addressfield.getName());

              UltaField ultaPickupField = null;
              if (addressfield.getName().equalsIgnoreCase(UltaCheckoutConstants.PHONE_NUM)) {
                ultaPickupField =
                    (UltaField) ReflectionUtils.getField(addressfield, ultaPickupInfo);
                if (ultaPickupField.getValue() != null
                    && ultaPickupField.getValue().contains(UltaCheckoutConstants.DASH)) {
                  ultaPickupField.setValue(
                      ultaPickupField.getValue().replace(UltaCheckoutConstants.DASH, ""));
                }
              } else {
                ultaPickupField =
                    (UltaField) ReflectionUtils.getField(addressfield, ultaPickupInfo);
              }
              UltaAddressValidationResponse ultaAddressValidationResponse =
                  validatePickupDetails(ultaPickupField, addressfield.getName(), deliveryMethod);

              if (!ultaAddressValidationResponse.isValid()) {
                ultaPickupField.setMessages(
                        new ArrayList<>(Arrays.asList(
                                UltaMessage.builder()
                                        .type(UltaMessage.UltaMessageType.ERROR.getValue())
                                        .code(
                                                UltaResourceManager.getMessageKey(
                                                        ultaAddressValidationResponse.getMessage().getCode()))
                                        .build())));
              }
              return ultaPickupField;
            })
        .collect(Collectors.toList());
  }


}