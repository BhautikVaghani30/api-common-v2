/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.buy.checkout.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ulta.dsp.common.domain.data.UltaShipping;
import com.ulta.dsp.common.domain.data.UltaShippingDetails;
import com.ulta.dsp.common.utils.UltaJsonParserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.ulta.dsp.buy.checkout.model.common.UltaCorrectedAddress;
import com.ulta.dsp.buy.checkout.model.common.UltaData;
import com.ulta.dsp.buy.checkout.model.core.response.UltaCoreResponse;
import com.ulta.dsp.buy.checkout.model.orch.request.UltaAddressRequest;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.dav.client.model.UltaDAVAddress;
import com.ulta.dsp.dav.client.model.UltaDAVExtAddress;

/**
 * Util file help to perform some operation for Doctor address validation like prepare
 * UltaDAVAddress request etc.
 */
public final class UltaDAVUtils {
  private static final Logger LOG = LoggerFactory.getLogger(UltaDAVUtils.class);

  private UltaDAVUtils() {}

  /**
   * Method is used to help for preparing UltaDAVAddress request to pass in Doctor address
   * validation system.
   *
   * @param ultaAddressRequest
   * @return UltaDAVAddress
   */
  public static UltaDAVAddress prepareUltaDAVAddressRequest(UltaAddressRequest ultaAddressRequest) {
    return UltaDAVAddress.of(
        ultaAddressRequest.getAddress1(),
        ultaAddressRequest.getAddress2(),
        ultaAddressRequest.getCity(),
        ultaAddressRequest.getState(),
        ultaAddressRequest.getZipCode(),
        ultaAddressRequest.getCountry());
  }

  /**
   * Method is used to check existing shipping address is same or not.
   *
   * @param ultaDataUltaCoreResponse
   * @param ultaAddressRequest
   * @return int 1 or 0
   */
  public static int isExistingShippingAddressSame(
      UltaCoreResponse<UltaData> ultaDataUltaCoreResponse, UltaAddressRequest ultaAddressRequest) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(
          "Entity Name : UltaDAVUtils - Entered Process Name : isExistingShippingAddressSame - UltaCoreResponse : {} - ",
          UltaJsonParserUtils.getJsonFromObject(ultaDataUltaCoreResponse));
    }
    UltaAddress ultaShippingAddress =
            Optional.ofNullable(ultaDataUltaCoreResponse)
                    .map(UltaCoreResponse::getData)
                    .map(UltaData::getShipping)
                    .map(UltaShipping::getOnline)
                    .filter(shippingOnline -> !CollectionUtils.isEmpty(shippingOnline))
                    .map(shippingOnline -> shippingOnline.get(0))
                    .map(UltaShippingDetails::getShippingAddress)
                    .orElse(null);
    UltaAddressRequest existingUltaAddressRequest = new UltaAddressRequest();
    if (null != ultaShippingAddress) {
      if (UltaShippingUtils.isNotNull(ultaShippingAddress.getAddress1())) {
        existingUltaAddressRequest.setAddress1(ultaShippingAddress.getAddress1().getValue());
      }
      if (UltaShippingUtils.isNotNull(ultaShippingAddress.getAddress2())) {
        existingUltaAddressRequest.setAddress2(ultaShippingAddress.getAddress2().getValue());
      }
      if (UltaShippingUtils.isNotNull(ultaShippingAddress.getCountry())) {
        existingUltaAddressRequest.setCountry(ultaShippingAddress.getCountry().getValue());
      }
      if (UltaShippingUtils.isNotNull(ultaShippingAddress.getState())) {
        existingUltaAddressRequest.setState(ultaShippingAddress.getState().getValue());
      }
      if (UltaShippingUtils.isNotNull(ultaShippingAddress.getCity())) {
        existingUltaAddressRequest.setCity(ultaShippingAddress.getCity().getValue());
      }
      if (UltaShippingUtils.isNotNull(ultaShippingAddress.getZipCode())) {
        existingUltaAddressRequest.setZipCode(ultaShippingAddress.getZipCode().getValue());
      }
    } else {
      LOG.error(
          "Entity Name : UltaDAVUtils - Entered Process Name : isExistingShippingAddressSame - ShippingAddress( is NULL or Empty) : {}",
          ultaShippingAddress);
    }
    try {
      if (haveSamePropertyValues(
          UltaAddressRequest.class, existingUltaAddressRequest, ultaAddressRequest)) {
        return 0;
      }
    } catch (Exception e) {
      LOG.error("haveSamePropertyValues :: exception : {}", e);
    }
    return 1;
  }

  /**
   * Method is used to populate corrected address based on doctor address validator.
   *
   * @param listOfultaDAVExtAddress
   * @return List<UltaCorrectedAddress>
   */
  public static List<UltaCorrectedAddress> populateCorrectedAddressList(
      List<UltaDAVExtAddress> listOfultaDAVExtAddress) {
    List<UltaCorrectedAddress> listOfUltaCorrectedAddress = new ArrayList<>();
    if (!CollectionUtils.isEmpty(listOfultaDAVExtAddress)) {
      listOfultaDAVExtAddress.forEach(
          ultaDAVExtAddress -> {
            UltaCorrectedAddress ultaCorrectedAddress = new UltaCorrectedAddress();
            ultaCorrectedAddress.setAddress1(ultaDAVExtAddress.getAddress1());
            ultaCorrectedAddress.setAddress2(ultaDAVExtAddress.getAddress2());
            ultaCorrectedAddress.setCity(ultaDAVExtAddress.getCity());
            ultaCorrectedAddress.setCountry(ultaDAVExtAddress.getCountry());
            ultaCorrectedAddress.setState(ultaDAVExtAddress.getState());
            ultaCorrectedAddress.setZipCode(ultaDAVExtAddress.getZipCode());
            listOfUltaCorrectedAddress.add(ultaCorrectedAddress);
          });
    }

    return listOfUltaCorrectedAddress;
  }

  public static <T> boolean haveSamePropertyValues(Class<T> type, T t1, T t2)
      throws IntrospectionException, InvocationTargetException, IllegalAccessException {
    BeanInfo beanInfo = Introspector.getBeanInfo(type);
    for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
      Method m = pd.getReadMethod();
      Object o1 = m.invoke(t1);
      Object o2 = m.invoke(t2);
      if (!Objects.equals(o1, o2)) {
        return false;
      }
    }
    return true;
  }
}