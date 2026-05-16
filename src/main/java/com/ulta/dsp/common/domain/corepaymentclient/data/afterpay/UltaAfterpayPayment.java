/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.afterpay;

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
@AllArgsConstructor
@Builder
public class UltaAfterpayPayment implements UltaBasePaymentWithOrderId {

  private String id;

  private UltaAfterpayTokenInfo tokenInfo;

  private UltaAfterpayInstallmentInfo installmentInfo;

  private UltaPaymentTender afterPayPaymentTender;

  private List<UltaMessage> messages;

  private String redirectConfirmUrl;

  private String redirectCancelUrl;

  private boolean isNewTokenRequired;

  private String orderId;

  private String appliedMid;
}