/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UltaDiscountDetail {
  private String discountTypeCode;
  private String reductionAmount;
  private String promotionID;
  private String promotionLineNumber;
  private String discountNumber;
  private String couponCode;
  private String promotionPlatform;
}