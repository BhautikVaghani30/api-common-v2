/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UltaInventoryReservationDetails {
  private BigDecimal reservedQty;
  private String reservedSiteID;
  private String requestedReservationDate;

}