/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.corepaymentclient.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ulta.dsp.common.model.UltaMessage;
import com.ulta.dsp.common.security.internal.UltaMaskType;
import com.ulta.dsp.common.security.internal.UltaSensitive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder(builderClassName = "Builder")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UltaCreditCardInfo {

  private String id;
  private long version;
  private boolean primary;
  private UltaField tokenizedCard;
  private UltaField maskedPAN;
  private UltaField cardType;

  private UltaField expirationYear;

  private UltaField expirationMonth;

  private UltaField entityId;

  private List<UltaMessage> messages;

  @UltaSensitive(type = UltaMaskType.CVV)
  private UltaField cardVerificationNumber;

  private boolean virtualCreditCard;
  private UltaField networkTransactionId;

}