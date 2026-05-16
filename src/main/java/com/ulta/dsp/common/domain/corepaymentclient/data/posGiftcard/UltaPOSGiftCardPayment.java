package com.ulta.dsp.common.domain.corepaymentclient.data.posGiftcard;

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
public class UltaPOSGiftCardPayment {

    private UltaPOSGiftCardPayerInfo payerInfo;
    private UltaPaymentTender posGiftCardPaymentTender;
    private String method;
}