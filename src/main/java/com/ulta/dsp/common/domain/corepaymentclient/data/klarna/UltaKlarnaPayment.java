/*
 * Copyright (C) 2026 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.corepaymentclient.data.klarna;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.corepaymentclient.data.UltaBasePaymentWithOrderId;
import com.ulta.dsp.common.domain.corepaymentclient.data.UltaPaymentTender;
import com.ulta.dsp.common.model.UltaMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaKlarnaPayment implements UltaBasePaymentWithOrderId {
  private UltaKlarnaPayerInfo payerInfo;
  private UltaPaymentTender<UltaKlarnaAuthInfo> klarnaPaymentTender;
  private List<UltaMessage> messages;
  private String method;
  private String orderId;
  private String appliedMid;
  private boolean newTokenRequired;
  private String klarnaSessionRequest;
}