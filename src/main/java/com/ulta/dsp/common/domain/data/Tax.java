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

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tax {
    private Double amount;
    private String srcTaxAreaID;
    private String destnTaxAreaID;
    private String srcNodeID;
    private String taxSource;
    private String situs;
    private List<TaxDetail> taxDetails;
    private String adminOriginTaxAreaID;

}