/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;

import com.ulta.dsp.common.model.UltaMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeliveryFee {
  private Long centAmount;
  private String currencyCode;
  private List<UltaMessage> messages;
  private UltaTaxAttributes tax;
  private List<UltaAppliedPromotion> shippingPromotions;
}