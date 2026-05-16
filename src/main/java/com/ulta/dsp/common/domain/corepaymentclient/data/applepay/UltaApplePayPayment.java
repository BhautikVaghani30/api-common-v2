package com.ulta.dsp.common.domain.corepaymentclient.data.applepay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.corepaymentclient.data.UltaBasePaymentWithOrderId;
import com.ulta.dsp.common.domain.corepaymentclient.data.UltaPaymentTender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaApplePayPayment implements UltaBasePaymentWithOrderId {
    private UltaApplePayPayerInfo payerInfo;
    private UltaPaymentTender applePayPaymentTender;
    private String method;
    private String orderId;
    private String appliedMid;
}