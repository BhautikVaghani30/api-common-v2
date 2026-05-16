/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.paypal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.corepaymentclient.data.UltaBasePaymentWithOrderId;
import com.ulta.dsp.common.domain.corepaymentclient.data.UltaPaymentTender;
import com.ulta.dsp.common.model.UltaMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaPaypalPayment implements UltaBasePaymentWithOrderId {

  private UltaPaypalPayerInfo payerInfo;

  private UltaPaymentTender payPalPaymentTender;

  private UltaMessage message;

  private String method;

  private String orderId;

  private String appliedMid;
}