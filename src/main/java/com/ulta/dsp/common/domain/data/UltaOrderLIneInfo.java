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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UltaOrderLIneInfo {
  private UltaDiscount discounts;
  private Tax taxDetail;
  private String quantity;
  private String shipNode;
  private String salesAmount;
  private String totalSalesAmount;
  private FromAddress fromAddress;
}