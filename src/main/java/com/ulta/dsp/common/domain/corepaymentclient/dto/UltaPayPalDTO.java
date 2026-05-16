/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.corepaymentclient.dto;

import com.ulta.dsp.common.domain.corepaymentclient.data.UltaPaymentTender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class UltaPayPalDTO {

  private UltaPayerInfoDTO payerInfo;
  private UltaPaymentTender payPalPaymentTender;
  private String method;
}