package com.ulta.dsp.common.domain.corepaymentclient.data.posGiftcard;

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
public class UltaPOSGiftCardPayerInfo {

    private Long giftCardAmount;
    private String terminalId;
    private String authorizationInvoice;
    private String swipedata;
    private String giftCardNumber;
}