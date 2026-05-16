package com.ulta.dsp.common.domain.corepaymentclient.data.applepay;

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
public class UltaApplePayPayerInfo {
    private UltaApplePayAddress billingAddress;
    private UltaApplePayAddress shippingAddress;
    private String guestEmailAddress;
    private String paymentToken;
    private String creditCardType;
    private String creditCardNumber;
    private String applePayTranxId;
    private String paymentType;

}