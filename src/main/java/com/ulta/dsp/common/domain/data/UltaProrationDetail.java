/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(builderClassName = "Builder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UltaProrationDetail {
  List<UltaDiscountProration> discountProration;
  List<UltaItemShipmentProration> itemShipmentProration;
}