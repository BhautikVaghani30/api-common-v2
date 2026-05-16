/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import com.ulta.dsp.common.domain.data.loyalty.UltaRewards;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UltaSummary {

  private Long totalQuantity;
  private UltaPrice subTotal;
  private UltaTax tax;
  private UltaPrice retailDeliveryFee;
  private UltaPrice pifAmount;
  private UltaPrice couponDiscount;
  private UltaPrice promotionalDiscount;
  private VirtualCCDiscount virtualCCDiscount;
  private UltaPrice orderGiftWrapAmt;
  private ShippingCost shippingCost;
  private UltaPrice total;
  private UltaPrice totalWithoutTax;
  private UltaRewards rewards;
  private Double redeemedRewards;
  private UltaPrice afterPay;
  private UltaPrice loyalty;
  private UltaPrice giftCard;
  private UltaPrice creditCard;
  private UltaPrice payPal;
  private UltaPrice applePay;
  private UltaPrice venmo;
  private UltaPrice klarna;
  private DeliveryFee deliveryFee;
  private UltaPrice deliveryTip;
  private UltaPrice fiPay;
  private UltaPrice posGiftCard;
  private String previewDateTime;
  private UltaFulfillmentDetails fulfillmentDetails;
  private String pickupCost;
}