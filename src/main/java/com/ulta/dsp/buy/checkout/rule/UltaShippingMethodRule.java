/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.buy.checkout.rule;

import com.ulta.dsp.buy.checkout.constants.UltaCheckoutConstants;
import com.ulta.dsp.buy.checkout.model.rule.UltaEligibleShippingMethod;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.data.UltaSku;
import lombok.NoArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@NoArgsConstructor
@Component
public class UltaShippingMethodRule {
  @Autowired
  @Qualifier("shippingMethodRule")
  private KieContainer kContainer;

  @Autowired
  @Qualifier("shippingMethodRuleUS")
  private KieContainer kieContainer;

  @Value("${ulta.standard.shippingMethod.key}")
  private String standardShippingMethod;

  @Value("#{'${shipping.address.militaryStates}'.split(',')}")
  private List<String> militaryStates;

  @Value("#{'${shipping.address.nonContinentalStates}'.split(',')}")
  private List<String> nonContinentalStates;

  @Value("#{'${shipping.address.virginIslandStates}'.split(',')}")
  private List<String> virginIslandStates;

  @Value("#{${shipping.address.hazmatRestrictedPostalCodes}}")
  private List<String> hazmatRestrictedPostalCodes;

  @Value("#{${shipping.address.restrictedZipCodes}}")
  private List<String> restrictedZipCodes;

  @Value("#{'${shipping.address.poBoxStrings}'.split(',')}")
  private List<String> poBoxStrings;

  @Value("${shipping.address.pobox.regex}")
  private String poBoxRegex;

  @Value("#{'${shipping.address.apoCities}'.split(',')}")
  private List<String> apoCitiesStrings;

  public Mono<UltaEligibleShippingMethod> shippingMethodEligibilityForCA(
      UltaAddress ultaAddress, UltaSku ultaSku) {
    UltaEligibleShippingMethod ultaEligibleShippingMethod = new UltaEligibleShippingMethod();

    KieSession kieSession = null;
    try {
      kieSession = kContainer.newKieSession();
      kieSession.setGlobal("standardShippingMethod", standardShippingMethod);
      kieSession.insert(ultaAddress);
      kieSession.insert(ultaSku);
      kieSession.insert(ultaEligibleShippingMethod);
      kieSession.fireAllRules();
    } finally {
      if (kieSession != null) {
        kieSession.dispose();
      }
    }

    return Mono.just(ultaEligibleShippingMethod);
  }

  /**
   * Method is used to fire shipping method eligibility rules for US.
   *
   * @param ultaAddress
   * @param hazmat
   * @return ultaEligibleShippingMethod
   */
  public Mono<UltaEligibleShippingMethod> shippingMethodEligibilityForUS(
      UltaAddress ultaAddress,
      Boolean hazmat,
      Boolean fragrance,
      Boolean nativeItemsHazmat,
      Boolean nativeItemsFragrance,
      Boolean is3pItemInCart) {

    UltaEligibleShippingMethod ultaEligibleShippingMethod = new UltaEligibleShippingMethod();

    KieSession kieSession = null;
    try {
      kieSession = kieContainer.newKieSession();
      kieSession.setGlobal(UltaCheckoutConstants.STANDARD_SHIPPING_METHOD, standardShippingMethod);
      kieSession.setGlobal(UltaCheckoutConstants.HAZMAT, hazmat);
      kieSession.setGlobal(UltaCheckoutConstants.FRAGRANCE, fragrance);
      kieSession.setGlobal(UltaCheckoutConstants.NATIVE_ITEMS_HAZMAT, nativeItemsHazmat);
      kieSession.setGlobal(UltaCheckoutConstants.NATIVE_ITEMS_FRAGRANCE, nativeItemsFragrance);
      kieSession.setGlobal(UltaCheckoutConstants.ITEM_3P, is3pItemInCart);
      kieSession.setGlobal(UltaCheckoutConstants.MILITARY_STATES, militaryStates);
      kieSession.setGlobal(UltaCheckoutConstants.NON_CONTINENTAL_STATES, nonContinentalStates);
      kieSession.setGlobal(UltaCheckoutConstants.VIRGIN_ISLAND_STATES, virginIslandStates);
      kieSession.setGlobal(
          UltaCheckoutConstants.HAZMAT_RESTRICTED_POSTAL_CODES, hazmatRestrictedPostalCodes);
      kieSession.setGlobal(UltaCheckoutConstants.RESTRICTED_ZIP_CODES, restrictedZipCodes);
      kieSession.setGlobal(UltaCheckoutConstants.PO_BOX_REGEX, poBoxRegex);
      kieSession.setGlobal(UltaCheckoutConstants.PO_BOX_STRINGS, poBoxStrings);
      kieSession.setGlobal(UltaCheckoutConstants.APO_CITIES_STRINGS, apoCitiesStrings);
      kieSession.insert(ultaAddress);
      kieSession.insert(ultaEligibleShippingMethod);
      kieSession.fireAllRules();
    } finally {
      if (kieSession != null) {
        kieSession.dispose();
      }
    }

    return Mono.just(ultaEligibleShippingMethod);
  }
}