/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.corepaymentclient.data.afterpay.UltaAfterpayPayment;
import com.ulta.dsp.common.domain.corepaymentclient.data.applepay.UltaApplePayPayment;
import com.ulta.dsp.common.domain.corepaymentclient.data.fipay.UltaFiPayPayment;
import com.ulta.dsp.common.domain.corepaymentclient.data.giftcarts.GiftCards;
import com.ulta.dsp.common.domain.corepaymentclient.data.klarna.UltaKlarnaPayment;
import com.ulta.dsp.common.domain.corepaymentclient.data.loyalty.UltaLoyalty;
import com.ulta.dsp.common.domain.corepaymentclient.data.paypal.UltaPaypalPayment;
import com.ulta.dsp.common.domain.corepaymentclient.data.posGiftcard.UltaPOSGiftCardPayment;
import com.ulta.dsp.common.domain.corepaymentclient.data.venmo.UltaVenmoPayment;
import com.ulta.dsp.common.model.UltaMessage;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaPayment {

  private List<UltaCreditCard> creditCards;

  private UltaCreditCard virtualCreditCard;

  private UltaAfterpayPayment afterpay;

  private UltaPaypalPayment payPal;

  private UltaLoyalty loyalty;

  private UltaApplePayPayment applePay;

  private UltaFiPayPayment fiPay;

  private UltaVenmoPayment venmo;

  private UltaKlarnaPayment klarna;

  private List<GiftCards> giftCards;

  private UltaPOSGiftCardPayment posGiftCards;

  private String primaryPayment;

  private List<UltaMessage> messages;
}