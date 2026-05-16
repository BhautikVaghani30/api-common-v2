/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingMethod {
    private String name;
    private String tipAmount;
    private String deliveryTipProductClass;
    private String retailDeliveryFee;
    private String retailDeliveryProductClass;
    private Cost cost;
    private String productClass;
}