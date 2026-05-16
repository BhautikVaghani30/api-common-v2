/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "Builder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UltaDiscountProration {
  private Integer quantityProrated;
  private UltaPrice unitProratedPrice;
  private UltaPrice unitDiscountSharePrice;
  private UltaPrice unitLPSDiscountSharePrice;
  private UltaPrice unitTaxProrationPrice;
}