/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.fipay;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaFiPayPayerInfo {

    private String authorizationInvoice;
    private String paymentToken;
    private String options;
    private String fipayExpirationDate;
    private String preAuthTerminalId;
    private String cardExpirationYear;
    private String creditCardNumber;
    private String authorizationCode;
    private String needsReversal;
    private String customerSignature;
    private String swipeData;
    private String cardExpirationMonth;
    private String creditCardType;
    private String deviceRegisterId;
    private String paymentType;
}