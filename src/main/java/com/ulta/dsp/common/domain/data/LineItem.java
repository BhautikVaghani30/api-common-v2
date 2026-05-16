/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineItem {
    private String originalOrderedQty;
    private String orderedQty;
    private String orderLineKey;
    private String lineType;
    private String lineItemID;
    private String skuId;
    private List<UnitPrice> unitPrices;
    private SKUPrice prices;
    private List<UltaOrderLIneInfo> orderLineRtnInfo;
    private List<UltaShipNode> shipNode;
    private UltaAward awards;
    private UltaLineCharge lineCharges;
    private UltaLineTax lineTaxes;
    private String productClass;
}