/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProratedPromotionDetail {
    private String id;
    private UltaPrice unitProratedDiscountAmount;
    private UltaPrice unitProratedRemainingPrice;
    private String description;
    private String discountedQty;
    private String promotionSource;
    private String promotionUrl;
    private String promoType;
    private String discountLevel;
    private String businessPromoType;
}