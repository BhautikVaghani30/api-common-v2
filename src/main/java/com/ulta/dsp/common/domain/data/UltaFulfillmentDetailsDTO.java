/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UltaFulfillmentDetailsDTO {

  private Long qty;
  private UltaPrice subtotal;

  private UltaPrice retailDeliveryFee;
  private UltaPrice pifAmount;
  private UltaPrice totalTax;

  private Double pointsEarned;
  private Double pointsApplied;
  private UltaPrice pointsAppliedValue;
}