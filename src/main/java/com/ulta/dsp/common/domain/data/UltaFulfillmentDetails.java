/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UltaFulfillmentDetails {

  private UltaFulfillmentDetailsDTO shp;
  private UltaFulfillmentDetailsDTO pick;
  private UltaFulfillmentDetailsDTO sdd;
  private UltaFulfillmentDetailsDTO shp_3P;
}