/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data;

import com.ulta.dsp.common.model.UltaMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(builderClassName = "Builder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UltaCreditCard implements UltaBasePaymentWithOrderId {

  private UltaAddress billingAddress;

  private UltaCreditCardInfo creditCardInfo;

  private UltaPaymentTender creditCardTender;

  private List<UltaMessage> messages;

  private String orderId;

  private String appliedMid;
}