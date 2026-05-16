/*
 * Copyright (C) 2022 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.loyalty;

import com.ulta.dsp.common.domain.data.UltaPrice;
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
public class UltaLoyaltyAuthInfo {
    private double pointEarn;
    private double pointBalance;
    private double redeemPoints;
    private UltaPrice redeemValue;
    private double basePoints;
    private double pointNet;
}