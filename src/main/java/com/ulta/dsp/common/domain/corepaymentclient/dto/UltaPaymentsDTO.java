/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.dto;

import com.ulta.dsp.common.domain.corepaymentclient.data.UltaCreditCard;
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
public class UltaPaymentsDTO {

  private UltaCreditCardDTO creditCard;
  private UltaCreditCard virtualCreditCard;
  private UltaPayPalDTO payPal;
}