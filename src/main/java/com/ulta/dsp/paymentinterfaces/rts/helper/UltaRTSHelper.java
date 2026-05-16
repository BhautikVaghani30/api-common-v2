/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.paymentinterfaces.rts.helper;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.ulta.dsp.common.domain.data.UltaCreditCardAuthInfo;
import com.ulta.dsp.paymentinterfaces.model.common.UltaCartType;
import com.ulta.dsp.paymentinterfaces.utils.UltaJsonParserUtils;
import org.apache.commons.lang3.StringUtils;

import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.data.UltaCreditCard;
import com.ulta.dsp.paymentinterfaces.creditcard.model.common.UltaRTSRequest;
import com.ulta.dsp.paymentinterfaces.model.common.UltaOrderData;
import com.ulta.dsp.paymentinterfaces.properties.UltaRTSProperties;

public final class UltaRTSHelper {
  private static final Integer BOUND = 1000000;

  private UltaRTSHelper() {
    // Default Constructor
  }

  /**
   * This method is used to populate RTS authorize request
   *
   * @param ultaOrderData
   * @param ultaRTSProperties
   * @param ultaCreditCard
   * @param cartType
   * @return UltaRTSRequest
   */
  public static UltaRTSRequest populateAuthorizeRTSRequest(
      UltaOrderData ultaOrderData,
      UltaRTSProperties ultaRTSProperties,
      UltaCreditCard ultaCreditCard,
      UltaCartType cartType) {
    UltaRTSRequest ultaRTSRequest =
        populateCommonRTSRequest(ultaOrderData, ultaRTSProperties, ultaCreditCard, cartType);
    ultaRTSRequest.setTransactionType(ultaRTSProperties.getCredit());
    ultaRTSRequest.setTranType(ultaRTSProperties.getAuthTranType());
    return ultaRTSRequest;
  }

  /**
   * This method is used to populate common RTS authorize request
   *
   * @param ultaOrderData
   * @param ultaRTSProperties
   * @param ultaCreditCard
   * @param cartType
   * @return UltaRTSRequest
   */
  private static UltaRTSRequest populateCommonRTSRequest(
      UltaOrderData ultaOrderData,
      UltaRTSProperties ultaRTSProperties,
      UltaCreditCard ultaCreditCard, UltaCartType cartType) {
    String storeNumber = ultaRTSProperties.getStoreNumber();
    if (UltaCartType.NATIVE != cartType){
      storeNumber = ultaRTSProperties.getStoreNumber3P();
    }
    UltaRTSRequest ultaRTSRequest = new UltaRTSRequest();
    ultaRTSRequest.setStoreNumber(storeNumber);
    ultaRTSRequest.setTerminalNumber(ultaRTSProperties.getTerminalNumber());
    ultaRTSRequest.setRequestType(ultaRTSProperties.getCmd());
    ultaRTSRequest.setInvoice(ultaOrderData.getId());
    ultaRTSRequest.setAccountNumber(
        ultaCreditCard.getCreditCardInfo().getTokenizedCard().getValue());
    ultaRTSRequest.setOrderAmount(
        ultaOrderData.getSummary().getCreditCard().getCentAmount().toString());
    ultaRTSRequest.setMailOrderAVSData(constructMailOrderAVS(ultaCreditCard, ultaRTSProperties));
    ultaRTSRequest.setOptions(generateOptions(ultaRTSProperties));
    setDateAndTime(ultaRTSRequest, ultaRTSProperties.getDateFormat());
    return ultaRTSRequest;
  }

  /**
   * This method is used to set Options in RTS request
   *
   * @param ultaRTSProperties
   * @return String
   */
  private static String generateOptions(UltaRTSProperties ultaRTSProperties) {
    Integer number = new SecureRandom().nextInt(BOUND);
    return MessageFormat.format(ultaRTSProperties.getOptions(), number.toString()).trim();
  }

  private static UltaRTSRequest setDateAndTime(UltaRTSRequest ultaRTSRequest, String dateFormat) {
    LocalDateTime dateTime = LocalDateTime.now();
    ultaRTSRequest.setDate(dateTime.format(DateTimeFormatter.ofPattern(dateFormat)));
    LocalTime currentTime = dateTime.toLocalTime();
    DateTimeFormatter timeFormat = DateTimeeFormatter.ofPattern("HHmmss");
    ultaRTSRequest.setTime(currentTime.format(timeFormat));
    return ultaRTSRequest;
  }

  private static String constructMailOrderAVS(
      UltaCreditCard creditCard, UltaRTSProperties ultaRTSProperties) {
    UltaAddress billingAddress = creditCard.getBillingAddress();
    StringBuilder address1 = new StringBuilder();
    StringBuilder address2 = new StringBuilder();

    /*mail order avs data need to be populated with billing address and customer name.
     * Sample data : *ECI *AVS_zipCode_Address1-Address2-Address3
     * Space in the address to be separated by '-' (*ECI *AVS_80207_7800-Smith-Rd )*/

    if (null != billingAddress.getAddress1()
        && StringUtils.isNotEmpty(billingAddress.getAddress1().getValue())) {
      address1
          .append("_")
          .append(
              billingAddress
                  .getAddress1()
                  .getValue()
                  .trim()
                  .replaceAll(",", "")
                  .replaceAll(" ", "-"));
    }
    if (null != billingAddress.getAddress2()
        && StringUtils.isNotEmpty(billingAddress.getAddress2().getValue())) {
      address2
          .append("-")
          .append(billingAddress.getAddress2().getValue().trim().replaceAll(" ", "-"));
    }

    return MessageFormat.format(
            ultaRTSProperties.getMailOrderAVSData(),
            billingAddress.getZipCode().getValue(),
            address1.toString(),
            address2.toString())
        .trim();
  }

  /**
   * This method is used to populate RTS void request info
   *
   * @param ultaOrderData
   * @param ultaRTSProperties
   * @param ultaCreditCard
   * @param cartType
   * @return
   */
  public static UltaRTSRequest populateVoidRequest(
      UltaOrderData ultaOrderData,
      UltaRTSProperties ultaRTSProperties,
      UltaCreditCard ultaCreditCard,
      UltaCartType cartType) {
    UltaRTSRequest ultaRTSRequest =
        populateAuthorizeRTSRequest(ultaOrderData, ultaRTSProperties, ultaCreditCard, cartType);
    UltaCreditCardAuthInfo creditCardResponse =
        UltaJsonParserUtils.getObjectFromJson(
            UltaJsonParserUtils.getJsonFromObject(
                ultaCreditCard.getCreditCardTender().getAuthInfo()),
            UltaCreditCardAuthInfo.class);
    ultaRTSRequest.setTranType(ultaRTSProperties.getAuthVoidTranType());
    ultaRTSRequest.setAuthcode(creditCardResponse.getAuthId());
    ultaRTSRequest.setNeedsReversal(creditCardResponse.getNeedsReversal());
    return ultaRTSRequest;
  }
}