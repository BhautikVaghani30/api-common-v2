/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.corepaymentclient.data.giftcarts;

import com.ulta.dsp.common.domain.data.UltaPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UltaGiftCardRedemptionInfo {
  private String expirationDate;
  private String systemTraceNumber;
  private String authorizationCode;
  private String responseCode;
  private String orderId;
  private String embossedCardNumber;
  private UltaPrice previousBalance;
  private UltaPrice newBalance;
  private UltaPrice lockAmount;
  private String localCurrency;
}