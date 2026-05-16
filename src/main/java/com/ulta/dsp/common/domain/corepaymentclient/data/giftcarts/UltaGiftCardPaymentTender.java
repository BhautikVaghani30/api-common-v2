/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.giftcarts;

import com.ulta.dsp.common.domain.corepaymentclient.data.UltaDMResponse;
import com.ulta.dsp.common.model.UltaMessage;
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
public class UltaGiftCardPaymentTender {
    private UltaGiftCardRedemptionInfo authInfo;
    private String voidStatus;
    private UltaDMResponse dmResponse;
    private UltaMessage message;
}