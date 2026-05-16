/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.paypal;

import com.ulta.dsp.common.domain.data.UltaPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UltaPaypalAuthInfo {
  private String transactionStatus;
  private String transactionId;
  private String graphQLId;
  private String nonceType;
  private String payerId;
  private String orderId;
  private String payerStatus;
  private String paymentType;
  private String token;
  private String accountNumber;
  private String authorizationId;
  private UltaPrice authorizedAmount;
}