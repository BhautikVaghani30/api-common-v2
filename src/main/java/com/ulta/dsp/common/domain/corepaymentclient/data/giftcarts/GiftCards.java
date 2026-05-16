package com.ulta.dsp.common.domain.corepaymentclient.data.giftcarts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.corepaymentclient.data.UltaBasePaymentWithOrderId;
import com.ulta.dsp.common.domain.data.UltaPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftCards implements UltaBasePaymentWithOrderId {
    private String cardNumber;
    private String originalCardNumber;
    private String pin;
    private String encodedKey;
    private String encodedCardNumber;
    private UltaPrice amountApplied;
    private UltaPrice amountAvailable;
    private boolean isActive;
    private UltaGiftCardPaymentTender giftCardPaymentTender;
    private String id;
    private String orderId;
}