/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "Builder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UltaCreditCardAuthInfo {
  private String cvn;
  private String authId;
  private String orderId;
  private UltaPrice authorizedAmount;
  private String avs;
  private String status;
  private String needsReversal;
  private String reason;
  private String networkTransactionId;
}