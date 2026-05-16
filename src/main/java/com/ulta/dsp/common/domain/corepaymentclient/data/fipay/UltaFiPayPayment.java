/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.fipay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.corepaymentclient.data.UltaPaymentTender;
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
public class UltaFiPayPayment {

    private UltaFiPayPayerInfo payerInfo;
    private UltaPaymentTender fiPayPaymentTender;
    private String method;
}