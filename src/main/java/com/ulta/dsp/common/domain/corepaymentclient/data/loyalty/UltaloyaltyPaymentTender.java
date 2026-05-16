/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.loyalty;

import com.ulta.dsp.common.domain.corepaymentclient.data.UltaDMResponse;
import com.ulta.dsp.common.domain.data.UltaAddress;
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
public class UltaloyaltyPaymentTender {
    private UltaLoyaltyAuthInfo authInfo;
    private String voidStatus;
    private UltaDMResponse dmResponse;
}