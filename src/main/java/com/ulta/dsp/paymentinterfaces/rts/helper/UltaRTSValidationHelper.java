/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.paymentinterfaces.rts.helper;

import java.util.List;
import java.util.function.Function;

import com.ulta.dsp.common.domain.data.UltaCreditCard;
import com.ulta.dsp.common.domain.data.UltaCreditCardInfo;
import com.ulta.dsp.common.domain.data.UltaPaymentTender;
import com.ulta.dsp.common.domain.data.UltaCreditCardAuthInfo;
import com.ulta.dsp.common.domain.data.UltaSummary;
import com.ulta.dsp.paymentinterfaces.utils.UltaJsonParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.ulta.dsp.paymentinterfaces.constants.UltaErrorMessages;
import com.ulta.dsp.paymentinterfaces.model.common.UltaOrderData;
import com.ulta.dsp.paymentinterfaces.model.request.UltaAuthorizePaymentRequest;
import com.ulta.dsp.paymentinterfaces.model.request.UltaVoidPaymentRequest;
import com.ulta.dsp.paymentinterfaces.utils.UltaPaymentUtils;

@Slf4j
public final class UltaRTSValidationHelper {

  private UltaRTSValidationHelper() {
    // Default constructor for sonar issue
  }

  public static String validateAuthorizationRequest(
      UltaAuthorizePaymentRequest request, UltaCreditCard ultaCreditCard) {
    List<Function<UltaAuthorizePaymentRequest, String>> authorizePaymentRequestValidations =
        List.of(
            req -> validateOrderDraftId(req.getOrderDraft()),
            req -> validateOrderDraftSummary(req.getOrderDraft().getSummary()),
            req -> validateOrderDraftSummaryTotal(req.getOrderDraft().getSummary()),
            req -> validateCreditCard(ultaCreditCard));
    log.debug("Entity name - UltaRTSValidationHelper - authorizePaymentRequestValidations : {}",
            UltaJsonParserUtils.getJsonFromObject(authorizePaymentRequestValidations));
    return authorizePaymentRequestValidations.stream()
        .map(func -> func.apply(request))
        .filter(StringUtils::isNotEmpty)
        .findFirst()
        .orElse(null);
  }
  /**
   * Method to validate order draft id
   *
   * @param ultaOrderData
   * @return orderIdValidationError
   */
  private static String validateOrderDraftId(UltaOrderData ultaOrderData) {
    String orderIdValidationError = null;
    if (StringUtils.isEmpty(ultaOrderData.getId())) {
      orderIdValidationError = UltaErrorMessages.ERROR_ORDER_DRAFT_ID_REQUIRED;
    }
    return orderIdValidationError;
  }

  /**
   * Method to validate order Summary
   *
   * @param ultaSummary
   * @return orderSummaryValidationError
   */
  private static String validateOrderDraftSummary(UltaSummary ultaSummary) {
    String orderSummaryValidationError = null;
    if (null == ultaSummary) {
      orderSummaryValidationError = UltaErrorMessages.ERROR_ORDER_DRAFT_SUMMARY_REQUIRED;
    }
    return orderSummaryValidationError;
  }

  /**
   * Method to validate order draft summary total
   *
   * @param ultaSummary
   * @return orderTotalValidationError
   */
  private static String validateOrderDraftSummaryTotal(UltaSummary ultaSummary) {
    String orderTotalValidationError;
    if (null == ultaSummary.getTotal()) {
      orderTotalValidationError = UltaErrorMessages.ERROR_TOTAL_AMOUNT_REQUIRED;
    } else if (null == ultaSummary.getTotal().getCentAmount()) {
      orderTotalValidationError = UltaErrorMessages.ERROR_TOTAL_CENTAMOUNT_REQUIRED;
    } else {
      orderTotalValidationError = null;
    }
    return orderTotalValidationError;
  }

  /**
   * Method to validate mandatory credit card details
   *
   * @param ultaCreditCard
   * @return creditCardValidationError
   */
  private static String validateCreditCard(UltaCreditCard ultaCreditCard) {
    String creditCardValidationError = null;
    if (null == ultaCreditCard.getBillingAddress()) {
      creditCardValidationError = UltaErrorMessages.ERROR_BILLING_ADDRESS_REQUIRED;
    } else if (UltaPaymentUtils.isNullField(ultaCreditCard.getBillingAddress().getZipCode())) {
      creditCardValidationError = UltaErrorMessages.ERROR_BILLING_ADDRESS_ZIPCODE_REQUIRED;
    } else {
      creditCardValidationError = validateCreditCardInfo(ultaCreditCard.getCreditCardInfo());
    }

    return creditCardValidationError;
  }

  /**
   * Method to validate mandatory UltaCreditCardInfo details
   *
   * @param ultaCreditCardInfo
   * @return creditCardInfoValidationError
   */
  private static String validateCreditCardInfo(UltaCreditCardInfo ultaCreditCardInfo) {
    String creditCardInfoValidationError = null;
    if (UltaPaymentUtils.isNullField(ultaCreditCardInfo.getTokenizedCard())) {
      creditCardInfoValidationError = UltaErrorMessages.ERROR_TOKENIZEDCARD_REQUIRED;
    }
    return creditCardInfoValidationError;
  }

  /**
   * This method is used to Validate void payment RTS request
   *
   * @param request
   * @param creditCardPayment
   * @return
   */
  public static String validateVoidRTSRequest(
      UltaVoidPaymentRequest request, UltaCreditCard creditCardPayment) {
    List<Function<UltaVoidPaymentRequest, String>> voidPaymentRequestValidations =
        List.of(
            req -> validateOrderDraftId(req.getOrderDraft()),
            req -> validateOrderDraftSummary(req.getOrderDraft().getSummary()),
            req -> validateOrderDraftSummaryTotal(req.getOrderDraft().getSummary()),
            req -> validateCreditCard(creditCardPayment),
            req -> validateAuthInfo(creditCardPayment.getCreditCardTender()));
    return voidPaymentRequestValidations.stream()
        .map(func -> func.apply(request))
        .filter(StringUtils::isNotEmpty)
        .findFirst()
        .orElse(null);
  }

  /**
   * This method is used to validate mandatory
   *
   * @param ultaCreditCardTender
   * @return
   */
  private static String validateAuthInfo(
      UltaPaymentTender<UltaCreditCardAuthInfo> ultaCreditCardTender) {
    String creditCardValidationError;
    if (null == ultaCreditCardTender) {
      creditCardValidationError = UltaErrorMessages.ERROR_CREDIT_CARD_TENDER_REQUIRED;
    } else if (null == ultaCreditCardTender.getAuthInfo()) {
      creditCardValidationError = UltaErrorMessages.ERROR_CREDIT_CARD_AUTH_INFO_REQUIRED;
    } else if (StringUtils.isBlank(
        UltaJsonParserUtils.getObjectFromJson(
                UltaJsonParserUtils.getJsonFromObject(ultaCreditCardTender.getAuthInfo()),
                UltaCreditCardAuthInfo.class)
            .getNeedsReversal())) {
      creditCardValidationError = UltaErrorMessages.ERROR_CREDIT_AUTH_NEEDS_REVERSAL_REQUIRED;
    } else if (StringUtils.isBlank(
        UltaJsonParserUtils.getObjectFromJson(
                UltaJsonParserUtils.getJsonFromObject(ultaCreditCardTender.getAuthInfo()),
                UltaCreditCardAuthInfo.class)
            .getAuthId())) {
      creditCardValidationError = UltaErrorMessages.ERROR_CREDIT_CARD_AUTH_ID_REQUIRED;
    } else {
      creditCardValidationError = null;
    }
    return creditCardValidationError;
  }
}