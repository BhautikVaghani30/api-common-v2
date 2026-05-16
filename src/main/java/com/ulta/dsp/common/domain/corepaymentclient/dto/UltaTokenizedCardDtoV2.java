/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.security.internal.UltaMaskType;
import com.ulta.dsp.common.security.internal.UltaSensitive;
import com.ulta.dsp.common.validator.UltaNotEmptyCreditCardFields;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@UltaNotEmptyCreditCardFields(message = "{ERROR_EMPTY_CC_FIELDS}")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UltaTokenizedCardDtoV2 {

  private String tokenizedCard;

  private String cardType;

  private String maskedPAN;

  private String id;

  @UltaSensitive(type = UltaMaskType.CVV)
  private String cvv;

  private boolean primary;

  private boolean virtualCreditCard;
}