/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.dto;

import com.ulta.dsp.common.security.internal.UltaMaskType;
import com.ulta.dsp.common.security.internal.UltaSensitive;
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
public class UltaCardInfoDTO {

  private String tokenizedCard;
  private String cardType;
  private String expirationMonth;
  private String expirationYear;
  private String maskedPAN;
  @UltaSensitive(type = UltaMaskType.CVV)
  private String cvv;
  private boolean primary;
  private String id;
  private boolean virtualCreditCard;
}