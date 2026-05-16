/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.venmo;

import com.ulta.dsp.common.domain.data.UltaPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UltaVenmoAuthInfo {
  private String transactionStatus;
  private String transactionId;
  private String graphQLId;
  private String nonceType;
  private String userName;
  private String orderId;
  private String paymentType;
  private String accountNumber;
  private UltaPrice authorizedAmount;
}