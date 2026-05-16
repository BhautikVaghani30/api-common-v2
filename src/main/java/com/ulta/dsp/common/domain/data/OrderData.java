/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderData {
  private String id;
  private String documentType;
  private String headerKey;
  private String totalAmount;
  private String couponCode;
  private String orderSettledInOMS;
  private String loyaltyTotal;
  private String loyaltyMemberStatus;
  private String createdOn;
  private String origin;
  private String deliveryMethod;
  private String currencyCode;
  private Address address;
  private ShippingMethod shippingMethod;
  private UltaGiftWrap giftwrap;
  private List<Payment> payments;
  private UltaItem items;
}