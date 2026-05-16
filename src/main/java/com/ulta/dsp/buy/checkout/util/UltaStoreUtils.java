/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.buy.checkout.util;

import com.ulta.dsp.buy.checkout.constants.UltaCheckoutConstants;
import com.ulta.dsp.buy.checkout.model.orch.request.UltaDoorDashAddress;
import com.ulta.dsp.buy.checkout.model.orch.request.UltaDoorDashCustomer;
import com.ulta.dsp.buy.checkout.model.orch.request.UltaDoorDashRequest;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.data.UltaDeliveryContactInfo;
import com.ulta.dsp.common.domain.data.UltaStore;
import com.ulta.dsp.common.utils.UltaJsonParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Ulta Ulta Store Utils class is used to perform some store related operation prepare door dash
 * request.
 */
@Slf4j
@Component
public class UltaStoreUtils {

  public UltaStoreUtils() {
    // To avoid sonar issue.
  }

  public static UltaDoorDashRequest prepareDoorDashRequest(
      UltaStore storeResponse, UltaAddress shippingAddress, UltaDeliveryContactInfo profile) {

    log.debug("prepareDoorDashRequest :: storeResponse : {} profile: {} ", storeResponse, profile);
    UltaDoorDashRequest request = new UltaDoorDashRequest();
    request.setDelivery_time(Instant.now().toString());

    if (null != storeResponse) {
      UltaDoorDashAddress doorDashAddress =
          UltaDoorDashAddress.builder()
              .city(storeResponse.getStoreContactInfo().getCity().getValue())
              .street(storeResponse.getStoreContactInfo().getAddress1().getValue())
              .state(storeResponse.getStoreContactInfo().getState().getValue())
              .zip_code(storeResponse.getStoreContactInfo().getZipCode().getValue())
              .build();
      request.setPickup_address(doorDashAddress);
    }

    if (null != shippingAddress) {
      UltaDoorDashAddress doorDashDeliveryAddress =
          UltaDoorDashAddress.builder()
              .city(shippingAddress.getCity().getValue())
              .street(shippingAddress.getAddress1().getValue())
              .state(shippingAddress.getState().getValue())
              .zip_code(shippingAddress.getZipCode().getValue())
              .build();
      request.setDropoff_address(doorDashDeliveryAddress);
    }

    if (null != profile) {
      UltaDoorDashCustomer doorDashCustomer =
          UltaDoorDashCustomer.builder()
              .first_name(profile.getFirstName().getValue())
              .last_name(profile.getLastName().getValue())
              .email(profile.getEmailAddress().getValue())
              .phone_number(
                  UltaCheckoutConstants.COUNTRY_CODE.concat(profile.getPhoneNumber().getValue()))
              .build();
      request.setCustomer(doorDashCustomer);
    }
    if (log.isDebugEnabled()) {
      log.debug(
          "UltaStoreUtils :: prepareDoorDashRequest - request : {} ",
          UltaJsonParserUtils.getJsonFromObject(request));
    }
    return request;
  }
}