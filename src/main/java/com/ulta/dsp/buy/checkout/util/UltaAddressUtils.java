/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.buy.checkout.util;

import static com.ulta.dsp.buy.checkout.constants.UltaCheckoutConstants.DELIVERY_METHOD_SHP;
import static com.ulta.dsp.buy.checkout.constants.UltaCheckoutConstants.EMPTY;
import static com.ulta.dsp.buy.checkout.constants.UltaCheckoutConstants.THIRD_PARTY_PRODUCT_SUBTYPE;
import static com.ulta.dsp.buy.checkout.util.UltaCheckoutUtils.isDeliveryMethodApplicable;

import com.ulta.dsp.buy.checkout.config.UltaShippingAddressConfig;
import com.ulta.dsp.buy.checkout.constants.UltaCheckoutConstants;
import com.ulta.dsp.buy.checkout.constants.UltaErrorMessages;
import com.ulta.dsp.buy.checkout.i18n.UltaResourceManager;
import com.ulta.dsp.buy.checkout.model.common.UltaData;
import com.ulta.dsp.buy.checkout.model.core.response.UltaCoreResponse;
import com.ulta.dsp.buy.checkout.model.dto.UltaApplePayAddressInfoDTO;
import com.ulta.dsp.buy.checkout.model.orch.request.UltaAddressRequest;
import com.ulta.dsp.buy.checkout.model.orch.request.UltaApplePayRequestInfo;
import com.ulta.dsp.buy.checkout.model.orch.request.UltaContactInfoRequest;
import com.ulta.dsp.buy.checkout.model.orch.request.UltaOrchPaypalPaymentInfoRequest;
import com.ulta.dsp.buy.checkout.model.orch.request.UltaVenmoPaymentInfoRequest;
import com.ulta.dsp.buy.checkout.model.orch.response.UltaAddressData;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.data.UltaField;
import com.ulta.dsp.common.domain.data.UltaGift;
import com.ulta.dsp.common.domain.data.UltaGiftItem;
import com.ulta.dsp.common.domain.data.UltaPickupDetails;
import com.ulta.dsp.common.domain.data.UltaShippingMethod;
import com.ulta.dsp.common.model.UltaMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

/** Util class to perform some operation related to address like address is related to P.O BOX. */
public final class UltaAddressUtils {
  private static final char[] CHAR_STRIP_FOR_PO = {' ', '.', '-', '#', '@'};
  private static final List<String> HAZMAT_ITEMS = Arrays.asList("H");
  private static final List<String> FRAGRANCE_ITEMS = Arrays.asList("F");

  private UltaAddressUtils() {}

  /**
   * finds if address looks like one of the poBoxTemplates.
   *
   * @param pStreet street address
   * @return true / false
   */
  public static boolean isPOBox(String pStreet, List<String> poBoxStrings, String regex) {
    if (!StringUtils.isEmpty(pStreet)) {
      String address = strip(pStreet, CHAR_STRIP_FOR_PO);
      String poFormat = "";
      int aiSize = poBoxStrings.size();
      for (int idx = 0; idx < aiSize; idx++) {
        poFormat = poBoxStrings.get(idx).toUpperCase();
        if (StringUtils.isNotEmpty(address) && address.toUpperCase().indexOf(poFormat) != -1) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * finds if zip code field of address is one of zip code listed in the Shipping Restrictions zip
   * codes Hazmat Restricted Postal Code
   *
   * @param postalCode
   * @return true / false
   */
  public static boolean isHRPC(String postalCode, List<String> zipCodes) {
    String zipcode = strip(postalCode, CHAR_STRIP_FOR_PO);
    String poFormat = "";
    int aiSize = zipCodes.size();
    for (int idx = 0; idx < aiSize; idx++) {
      poFormat = zipCodes.get(idx);
      if (StringUtils.isNotEmpty(zipcode) && zipcode.indexOf(poFormat) != -1) {
        return true;
      }
    }
    return false;
  }

  /**
   * finds if zip code field of address is one of zip code listed in the Shipping Restrictions zip
   * codes Restricted Zip Code
   *
   * @param zipCode
   * @return true / false
   */
  public static boolean isRZC(String zipCode, List<String> zipCodes) {
    String zipcode = strip(zipCode, CHAR_STRIP_FOR_PO);
    for (String zipCodeEntry : zipCodes) {
      if (StringUtils.isNotEmpty(zipcode) && zipcode.equals(zipCodeEntry)) {
        return true;
      }
    }
    return false;
  }

  /**
   * finds if state field of address is one of state listed in military region
   *
   * @param state
   * @return true / false
   */
  public static boolean isMilitary(String state, List<String> militaryStates) {
    String militaryCode = strip(state, CHAR_STRIP_FOR_PO);
    String poFormat = "";
    int aiSize = militaryStates.size();
    for (int idx = 0; idx < aiSize; idx++) {
      poFormat = militaryStates.get(idx).toUpperCase();
      if (StringUtils.isNotEmpty(militaryCode)
          && militaryCode.toUpperCase().indexOf(poFormat) != -1) {
        return true;
      }
    }
    return false;
  }

  /**
   * finds if state field of address is one of state listed in non continental region
   *
   * @param state
   * @return true / false
   */
  public static boolean isNonContinental(String state, List<String> nonContinentalStates) {
    String stateCode = strip(state, CHAR_STRIP_FOR_PO);
    String poFormat = "";
    int aiSize = nonContinentalStates.size();
    for (int idx = 0; idx < aiSize; idx++) {
      poFormat = nonContinentalStates.get(idx).toUpperCase();
      if (StringUtils.isNotEmpty(stateCode) && stateCode.toUpperCase().indexOf(poFormat) != -1) {
        return true;
      }
    }
    return false;
  }

  /**
   * finds if state field of address is one of state listed in Virgin Islands
   *
   * @param state
   * @return true / false
   */
  public static boolean isVirginIslands(String state, List<String> virginIslandStates) {
    String stateCode = strip(state, CHAR_STRIP_FOR_PO);
    String poFormat = "";
    int aiSize = virginIslandStates.size();
    for (int idx = 0; idx < aiSize; idx++) {
      poFormat = virginIslandStates.get(idx).toUpperCase();
      if (StringUtils.isNotEmpty(stateCode) && stateCode.toUpperCase().indexOf(poFormat) != -1) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method strips all the characters in stripCharSet array from the string
   *
   * @param pStripFrom stripCharFrom
   * @param pStripCharSet stripCharSet
   * @return String, that has been stripped off chars in Charset.
   */
  public static String strip(String pStripFrom, char[] pStripCharSet) {
    if (StringUtils.isBlank(pStripFrom)) {
      return "";
    }
    if (pStripCharSet == null) {
      return pStripFrom;
    }
    char[] charsToStrip = pStripFrom.toCharArray();
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < pStripFrom.length(); i++) {
      boolean match = false;
      for (int j = 0; j < pStripCharSet.length; j++) {
        if (charsToStrip[i] == pStripCharSet[j]) {
          match = true;
          break;
        }
      }
      if (!match) {
        result.append(charsToStrip[i]);
      }
    }
    return result.toString();
  }

  /**
   * Method is used to check shipping address is correct or not.
   *
   * @param shippingAddress
   * @return
   */
  public static boolean isInCorrectShippingAddress(UltaAddress shippingAddress) {
    return (checkForAddress1AndCity(shippingAddress)
        || checkForStateAndCountry(shippingAddress)
        || checkForZipCodeAndPhoneNumber(shippingAddress));
  }

  /**
   * Method is used to check shipping address is Normal or not.
   *
   * @param shippingAddress
   * @return
   */
  public static boolean isNormalShippingAddress(
      UltaAddress shippingAddress,
      List<String> poBoxStrings,
      List<String> hazmatRestrictedPostalCodes,
      List<String> restrictedZipCodes,
      List<String> militaryStates,
      List<String> nonContinentalStates,
      List<String> virginIslandStates,
      String poBoxRegex) {
    return (!isPOBox(Optional.ofNullable(shippingAddress.getAddress1()).isPresent()
                ? shippingAddress.getAddress1().getValue() : EMPTY, poBoxStrings, poBoxRegex)
        && !isNonContinental(Optional.ofNullable(shippingAddress.getState()).isPresent()
            ? shippingAddress.getState().getValue() : EMPTY, nonContinentalStates)
        && !isMilitary(Optional.ofNullable(shippingAddress.getState()).isPresent()
            ? shippingAddress.getState().getValue() : EMPTY, militaryStates)
        && !isVirginIslands(Optional.ofNullable(shippingAddress.getState()).isPresent()
            ? shippingAddress.getState().getValue() : EMPTY, virginIslandStates)
        && !isRZC(Optional.ofNullable(shippingAddress.getZipCode()).isPresent()
            ? shippingAddress.getZipCode().getValue() : EMPTY, restrictedZipCodes)
        && !isHRPC(Optional.ofNullable(shippingAddress.getZipCode()).isPresent()
            ? shippingAddress.getZipCode().getValue() : EMPTY, hazmatRestrictedPostalCodes));
  }

  /**
   * Check for Address1 and City.
   *
   * @param shippingAddress
   * @return
   */
  private static boolean checkForAddress1AndCity(UltaAddress shippingAddress) {
    return (null != shippingAddress.getAddress1().getMessages()
        || null != shippingAddress.getCity().getMessages());
  }

  /**
   * Check for state and country.
   *
   * @param shippingAddress
   * @return
   */
  private static boolean checkForStateAndCountry(UltaAddress shippingAddress) {
    return (null != shippingAddress.getState().getMessages()
        || null != shippingAddress.getCountry().getMessages());
  }

  /**
   * Check for zip code and phone number.
   *
   * @param shippingAddress
   * @return
   */
  private static boolean checkForZipCodeAndPhoneNumber(UltaAddress shippingAddress) {
    return (null != shippingAddress.getZipCode().getMessages()
        || null != shippingAddress.getPhoneNumber().getMessages());
  }

  /**
   * Determines whether the cart's regular line items contain any HAZMAT items, considering only
   * items with a SHIP fulfillment type.
   *
   * <p>This method iterates through the {@code lineItems} within the provided {@code UltaData} and
   * checks if any of these items:
   *
   * <ul>
   *   <li>Are designated for shipping (i.e., their fulfillment type is {@code
   *       DELIVERY_METHOD_SHP}).
   *   <li>Have a SKU whose {@code hazmatCode} is included in the predefined {@code HAZMAT_ITEMS}
   *       list.
   * </ul>
   *
   * @param ultaData the cart data containing the line items to be checked.
   * @return {@code true} if any shippable line item is a HAZMAT SKU; {@code false} otherwise.
   *     Returns {@code false} immediately if {@code ultaData} is null or contains no line items.
   */
  public static boolean isLineItemsContainHazmatItems(UltaData ultaData) {
    if (null != ultaData && !CollectionUtils.isEmpty(ultaData.getLineItems())) {
      return ultaData.getLineItems().stream()
          .filter(
              ultaLineItem ->
                  DELIVERY_METHOD_SHP.equalsIgnoreCase(ultaLineItem.getFulfillmentType()))
          .anyMatch(ultaLineItem -> HAZMAT_ITEMS.contains(ultaLineItem.getSku().getHazmatCode()));
    }
    return false;
  }

  /**
   * Check for hazmat code is H for Native only.
   *
   * @param ultaData
   * @return
   */
  public static boolean isHazmatNativeItemsInCart(UltaData ultaData) {
    if (null != ultaData && !CollectionUtils.isEmpty(ultaData.getLineItems())) {
      return ultaData.getLineItems().stream()
          .filter(ultaLineItem -> DELIVERY_METHOD_SHP.equalsIgnoreCase(ultaLineItem.getFulfillmentType()))
          .filter(ultaLineItem -> !THIRD_PARTY_PRODUCT_SUBTYPE.equalsIgnoreCase(ultaLineItem.getSubType()))
          .anyMatch(ultaLineItem -> HAZMAT_ITEMS.contains(ultaLineItem.getSku().getHazmatCode()));
    }
    return false;
  }

  /**
   * Determines whether the cart contains any HAZMAT items, either in native line items or in
   * associated Gift With Purchase (GWP) items, considering only items with SHIP fulfillment type.
   *
   * <p>This method checks:
   *
   * <ul>
   *   <li>Regular line items — identifies whether any SHIP line item has a SKU whose hazmatCode is
   *       included in the configured {@code HAZMAT_ITEMS} list.
   *   <li>Gift items (GWP) — evaluates whether any SHIP fulfillment gift item has a hazmatCode that
   *       is classified as HAZMAT.
   * </ul>
   *
   * <p>Returns {@code true} if either a line item or a gift item is determined to be HAZMAT,
   * otherwise returns {@code false}. This is used to understand whether the cart contains any
   * hazardous material items that may be restricted based on the customer's selected shipping
   * address.
   *
   * @param ultaData the cart data containing line items and gift items
   * @return {@code true} if any SHIP line item or SHIP gift item is a HAZMAT SKU; {@code false}
   *     otherwise
   */
  public static boolean isCartContainsHazmatLineOrGiftItems(UltaData ultaData) {
    if (ultaData == null) {
      return false;
    }
    return isLineItemsContainHazmatItems(ultaData) || IsGiftItemsContainHazmatItems(ultaData);
  }

  /**
   * Checks if the cart's gift items contain any HAZMAT items, considering only items with SHIP
   * fulfillment type.
   *
   * @param ultaData the cart data containing gift items
   * @return {@code true} if any SHIP gift item is a HAZMAT SKU; {@code false} otherwise
   */
  private static boolean IsGiftItemsContainHazmatItems(UltaData ultaData) {
    return Optional.ofNullable(ultaData)
        .map(UltaData::getGiftItem)
        .map(UltaGift::getItems)
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .filter(giftItem -> DELIVERY_METHOD_SHP.equalsIgnoreCase(giftItem.getFulfillmentType()))
        .map(UltaGiftItem::getHazmatCode)
        .filter(Objects::nonNull)
        .anyMatch(HAZMAT_ITEMS::contains);
  }

  /**
   * Check for Hazmat code is F.
   *
   * @param ultaData
   * @return
   */
  public static boolean isFragranceCart(UltaData ultaData) {
    if (null != ultaData && !CollectionUtils.isEmpty(ultaData.getLineItems())) {
      return ultaData.getLineItems()
          .stream()
          .filter(ultaLineItem -> DELIVERY_METHOD_SHP.equalsIgnoreCase(ultaLineItem.getFulfillmentType()))
          .anyMatch(
              ultaLineItem -> FRAGRANCE_ITEMS.contains(ultaLineItem.getSku().getHazmatCode()));
    }
    return false;
  }

  /**
   * Check for Hazmat code is F for Native Items Only.
   *
   * @param ultaData
   * @return
   */
  public static boolean isFragranceNativeItemsInCart(UltaData ultaData) {
    if (null != ultaData && !CollectionUtils.isEmpty(ultaData.getLineItems())) {
      return ultaData.getLineItems()
          .stream()
          .filter(ultaLineItem -> DELIVERY_METHOD_SHP.equalsIgnoreCase(ultaLineItem.getFulfillmentType()))
          .filter(ultaLineItem -> !THIRD_PARTY_PRODUCT_SUBTYPE.equalsIgnoreCase(ultaLineItem.getSubType()))
          .anyMatch(
              ultaLineItem -> FRAGRANCE_ITEMS.contains(ultaLineItem.getSku().getHazmatCode()));
    }
    return false;
  }

  /**
   * Method to return selected shipping address from the Cart
   *
   * @param ultaData
   * @return
   */
  public static UltaAddress getShippingAddress(UltaData ultaData) {
    return Optional.ofNullable(ultaData)
        .map(UltaData::getShipping)
        .map(shipping -> shipping.getOnline())
        .filter(onlineLst -> !CollectionUtils.isEmpty(onlineLst))
        .map(onlineList -> onlineList.get(0))
        .map(onlineshipping -> onlineshipping.getShippingAddress())
        .orElse(null);
  }

  /**
   * Method is used to validate whether the shipping address is invalid or not.
   *
   * @param ultaAddress - the shipping address to validate
   * @return boolean - true if the shipping address is invalid, false otherwise
   */
  public static boolean isInvalidShippingAddress(UltaAddress ultaAddress) {
    if (Objects.isNull(ultaAddress)) {
      return true;
    }
    return Stream.of(
            ultaAddress.getAddress1(),
            ultaAddress.getCity(),
            ultaAddress.getState(),
            ultaAddress.getZipCode())
        .anyMatch(
            field ->
                Optional.ofNullable(field)
                    .map(UltaField::getValue)
                    .map(StringUtils::isBlank)
                    .orElse(true));
  }

  public static boolean isShippingAddressAddressExist(UltaCoreResponse<UltaData> response) {
    return Optional.ofNullable(response)
        .map(UltaCoreResponse::getData)
        .map(UltaAddressUtils::getShippingAddress)
        .map(UltaAddress::getAddress1)
        .map(UltaField::getValue)
        .isPresent();
  }

  /**
   * Method to return boolean based on shipping address verification
   *
   * @param ultaData
   * @return boolean
   */
  public static boolean validateShippingAddressDetails(UltaData ultaData) {
    UltaAddress shippingAddress = getShippingAddress(ultaData);
    if (shippingAddress == null) {
      return false;
    }
    return Stream.of(
            shippingAddress.getAddress1(),
            shippingAddress.getCity(),
            shippingAddress.getState(),
            shippingAddress.getZipCode())
        .allMatch(
            field -> {
              // All need to be not blank
              return StringUtils.isNotBlank(
                  Optional.ofNullable(field)
                    .map(UltaField::getValue)
                      .orElse(null)
              );
            });
  }

  /**
   * Method to return boolean based on billing address verification
   *
   * @param requestInfo
   * @return boolean
   */
  public static boolean validateBillingAddressDetails(UltaApplePayRequestInfo requestInfo) {
    return Optional.ofNullable(requestInfo)
        .map(req -> req.getPayerInfo())
        .map(payerInfo -> payerInfo.getBillingAddress())
        .map(
            billingAddress ->
                StringUtils.isNoneBlank(billingAddress.getAddressLine1())
                    && StringUtils.isNoneBlank(billingAddress.getCity())
                    && StringUtils.isNoneBlank(billingAddress.getState())
                    && billingAddress.getState().length() == DEFAULT_STATE_CODE_LENGTH
                    && StringUtils.isNoneBlank(billingAddress.getPostalCode()))
        .orElse(false);
  }

  /**
   * Method to return selected pickup details  from the Cart
   *
   * @param ultaData
   * @return
   */
  public static UltaPickupDetails getPickupDetails(UltaData ultaData) {
    if (null != ultaData && null != ultaData.getPickup()) {
      return ultaData.getPickup();
    }
    return null;
  }

  /**
   * Method to verify shipping object is present or not
   *
   * @param ultaData
   * @return boolean
   */
  public static boolean isShippingExist(UltaData ultaData) {
    return (null != ultaData
        && null != ultaData.getShipping()
        && !CollectionUtils.isEmpty(ultaData.getShipping().getOnline()));
  }

  /**
   * Method to verify payments object is present or not
   *
   * @param ultaData
   * @return boolean
   */
  public static boolean isPaymentExist(UltaData ultaData) {
    return (null != ultaData && null != ultaData.getPayment());
  }

  /**
   * Method to verify OrderDrafts is present or not
   *
   * @param ultaData
   * @return boolean
   */
  public static boolean isOrderDraftsExist(UltaData ultaData) {
    return (isPaymentExist(ultaData)
        || (isShippingExist(ultaData) && null != ultaData.getDeliveryMethod()));
  }

  /**
   * Method is used to check the profileAddress and billingAddress is same or not
   *
   * @param profileAddress
   * @param billingAddress
   * @return
   */
  public static boolean matchAddress(
      UltaAddressData profileAddress, UltaContactInfoRequest billingAddress) {
    if ((null != profileAddress.getFirstName()
            && profileAddress
                .getFirstName()
                .getValue()
                .equalsIgnoreCase(billingAddress.getFirstName()))
        && (null != profileAddress.getLastName()
            && profileAddress
                .getLastName()
                .getValue()
                .equalsIgnoreCase(billingAddress.getLastName()))
        && (null != profileAddress.getPhoneNumber()
            && profileAddress
                .getPhoneNumber()
                .getValue()
                .equalsIgnoreCase(billingAddress.getPhoneNumber()))
        && (null != profileAddress.getAddress1()
            && profileAddress
                .getAddress1()
                .getValue()
                .equalsIgnoreCase(billingAddress.getAddress().getAddress1()))
        && null != profileAddress.getAddress2()
        && StringUtils.equalsIgnoreCase(
            profileAddress.getAddress2().getValue(), billingAddress.getAddress().getAddress2())
        && (null != profileAddress.getCity()
            && profileAddress
                .getCity()
                .getValue()
                .equalsIgnoreCase(billingAddress.getAddress().getCity()))
        && (null != profileAddress.getState()
            && profileAddress
                .getState()
                .getValue()
                .equalsIgnoreCase(billingAddress.getAddress().getState()))
        && (null != profileAddress.getZipCode()
            && profileAddress
                .getZipCode()
                .getValue()
                .equalsIgnoreCase(billingAddress.getAddress().getZipCode()))) {
      return true;
    }

    return false;
  }

  /**
   * Method used to set invalid address message in checkout response.
   *
   * @param response
   * @return Mono<UltaCoreResponse < UltaData>>
   */
  public static Mono<UltaCoreResponse<UltaData>> buildInvalidVertexResponse(
      UltaCoreResponse<UltaData> response) {
    String code =
        UltaResourceManager.getCode(
            UltaResourceManager.getMessageKey(UltaErrorMessages.ADDRESS_VALIDATION_FAIL));
    if (StringUtils.isEmpty(code)) {
      code = UltaErrorMessages.ADDRESS_VALIDATION_FAIL;
    }
    if (!CollectionUtils.isEmpty(
        response.getData().getShipping().getOnline().get(0).getMessages())) {
      response
          .getData()
          .getShipping()
          .getOnline()
          .get(0)
          .getMessages()
          .add(
              UltaMessage.builder()
                  .type(UltaMessage.UltaMessageType.ERROR.getValue())
                  .code(code)
                  .build());
    } else {
      List<UltaMessage> listOfUltaMessage = new ArrayList<>();
      listOfUltaMessage.add(
          UltaMessage.builder()
              .type(UltaMessage.UltaMessageType.ERROR.getValue())
              .code(code)
              .build());
      response.getData().getShipping().getOnline().get(0).setMessages(listOfUltaMessage);
    }
    return Mono.just(response);
  }

  /**
   * Method used to check if email field is under shipping section
   *
   * @param fieldName
   * @param addressType
   * @return Mono<UltaCoreResponse < UltaData>>
   */
  public static boolean isEmailFieldForShippingAddress(String fieldName, String addressType) {
    return UltaCheckoutConstants.EMAIL.equalsIgnoreCase(fieldName)
        && UltaCheckoutConstants.ADDRESS_TYPE_SHIPPING.equalsIgnoreCase(addressType);
  }

  /**
   * finds if address has APO, FPO or DPO in city.
   *
   * @param city the city
   * @param apoCities the list of apo
   * @return true / false
   */
  public static boolean isAPOBox(String city, List<String> apoCities) {
    String apoFormat;
    for (String apoCity : apoCities) {
      apoFormat = apoCity.toUpperCase();
      if (StringUtils.isNotEmpty(city) && city.toUpperCase().equals(apoFormat)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Setting messages if address has APO, FPO, DPO in city and HI in state.
   *
   * @param coreCartResponse
   * @return UltaCoreResponse<UltaData>
   */
  public static UltaCoreResponse<UltaData> setMessagesWithAddress(
      UltaCoreResponse<UltaData> coreCartResponse) {
    if (null != coreCartResponse.getData()
        && null != coreCartResponse.getData().getShipping()
        && null != coreCartResponse.getData().getShipping().getOnline()
        && !CollectionUtils.isEmpty(coreCartResponse.getData().getShipping().getOnline())
        && null
            != coreCartResponse.getData().getShipping().getOnline().get(0).getShippingAddress()) {
      Boolean isAPO_FPO = false;
      Boolean is_NonContinental = false;
      Boolean is_POBox = false;
      Boolean is_VirginIsland = false;
      if (null
          != coreCartResponse
              .getData()
              .getShipping()
              .getOnline()
              .get(0)
              .getShippingAddress()
              .getCity()) {
        isAPO_FPO =
            UltaAddressUtils.isAPOBox(
                coreCartResponse
                    .getData()
                    .getShipping()
                    .getOnline()
                    .get(0)
                    .getShippingAddress()
                    .getCity()
                    .getValue(),
                UltaShippingAddressConfig.apoCities);
      }
      if (ObjectUtils.isNotEmpty(
          coreCartResponse
              .getData()
              .getShipping()
              .getOnline()
              .get(0)
              .getShippingAddress()
              .getState())) {
        is_NonContinental =
            UltaAddressUtils.isNonContinental(
                coreCartResponse
                    .getData()
                    .getShipping()
                    .getOnline()
                    .get(0)
                    .getShippingAddress()
                    .getState()
                    .getValue(),
                UltaShippingAddressConfig.nonContinentalStates);
        is_VirginIsland =
                UltaAddressUtils.isVirginIslands(
                        coreCartResponse
                                .getData()
                                .getShipping()
                                .getOnline()
                                .get(0)
                                .getShippingAddress()
                                .getState()
                                .getValue(),
                        UltaShippingAddressConfig.virginIslandStates);
      }
      if (null
          != coreCartResponse
              .getData()
              .getShipping()
              .getOnline()
              .get(0)
              .getShippingAddress()
              .getAddress1()) {
        is_POBox =
            UltaAddressUtils.isPOBox(
                coreCartResponse
                    .getData()
                    .getShipping()
                    .getOnline()
                    .get(0)
                    .getShippingAddress()
                    .getAddress1()
                    .getValue(),
                UltaShippingAddressConfig.poBoxStrings,
                UltaShippingAddressConfig.poboxRegex);
      }

      getShippingRestrictionCode(
              isAPO_FPO, is_NonContinental, is_POBox, is_VirginIsland, coreCartResponse.getData())
          .ifPresent(
              errorCode -> {
                if (isDeliveryMethodApplicable(
                    coreCartResponse.getData(), UltaCheckoutConstants.DELIVERY_METHOD_SHP)) {
                  UltaShippingMethod shippingMethod =
                      UltaShippingUtils.getApplicableShippingMethod(coreCartResponse.getData());

                  if (hasNoExistingErrorMessages(shippingMethod)) {
                    List<UltaMessage> infoMessage =
                        Collections.singletonList(
                            UltaMessage.builder()
                                .type(UltaMessage.UltaMessageType.INFO.getValue())
                                .code(errorCode)
                                .build());
                    UltaShippingUtils.updateShippingMethodsMessages(
                        coreCartResponse.getData(), infoMessage);
                    coreCartResponse
                        .getData()
                        .getSummary()
                        .getShippingCost()
                        .setMessages(infoMessage);
                  }
                }
              });
    }
    return coreCartResponse;
  }

  /**
   * Extract the billing address from a paypal payment request
   *
   * @param request
   * @return
   */
  public static UltaAddressRequest getBillingAddress(UltaOrchPaypalPaymentInfoRequest request) {

    return Optional.ofNullable(request)
        .map(UltaOrchPaypalPaymentInfoRequest::getPayerInfo)
        .map(payerInfo -> payerInfo.getBillingAddress())
        .orElse(null);
  }

  /**
   * Extract the billing address from a applePay payment request
   *
   * @param request
   * @return
   */
  public static UltaApplePayAddressInfoDTO getBillingAddress(UltaApplePayRequestInfo request) {
    return Optional.ofNullable(request)
        .map(UltaApplePayRequestInfo::getPayerInfo)
        .map(payerInfo -> payerInfo.getBillingAddress())
        .orElse(null);
  }

  /**
   * Extract the billing address from a venmo payment request
   *
   * @param venmoPaymentInfoRequest
   * @return
   */
  public static UltaAddressRequest getBillingAddress(
      UltaVenmoPaymentInfoRequest venmoPaymentInfoRequest) {
    return Optional.ofNullable(venmoPaymentInfoRequest)
        .map(UltaVenmoPaymentInfoRequest::getPayerInfo)
        .map(payerInfo -> payerInfo.getBillingAddress())
        .orElse(null);
  }

  /**
   * Determines the appropriate shipping restriction message code based on address type and cart
   * contents.
   *
   * @param isApoFpoOrPobox true if the address is an APO/FPO or PO Box.
   * @param isNonContinental true if the address is in a non-continental US state.
   * @param isPoBox true if the address is a PO Box.
   * @param isVirginIsland true if the address is in the US Virgin Islands.
   * @param cartData The cart data, used to check for hazmat items.
   * @return An {@link Optional} containing the error code, or empty if no restrictions apply.
   */
  private static Optional<String> getShippingRestrictionCode(
      boolean isApoFpoOrPobox,
      boolean isNonContinental,
      boolean isPoBox,
      boolean isVirginIsland,
      UltaData cartData) {
    if (isApoFpoOrPobox || isNonContinental || isPoBox || isVirginIsland) {
      return Optional.of(UltaCheckoutConstants.RESTRICTED_FREE_SHIPPING);
    } else if (isLineItemsContainHazmatItems(cartData)) {
      return Optional.of(UltaCheckoutConstants.RESTRICTED_FREE_SHIPPING_CONTIGUOUS_STATES);
    }
    return Optional.empty();
  }

  /**
   * Checks if the given shipping method has existing error messages.
   *
   * @param shippingMethod The shipping method to check.
   * @return true if there are no messages or the first message is not an error.
   */
  private static boolean hasNoExistingErrorMessages(UltaShippingMethod shippingMethod) {
    if (CollectionUtils.isEmpty(shippingMethod.getMessages())) {
      return true;
    }
    return !UltaMessage.UltaMessageType.ERROR
        .getValue()
        .equalsIgnoreCase(shippingMethod.getMessages().get(0).getType());
  }
}