/*
 * Copyright (C) 2019 ULTA
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

import java.util.List;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties("lineItemId")
public class UltaTaxAttributes {

  private UltaPrice itemTax;
  private UltaPrice pifAmount;
  private String destinationTaxAreaID;
  private String situs;
  private List<UltaTaxDetails> taxDetails;
  private String lineItemId;
  private String taxSource;
  private String srcTaxAreaID;
  private String adminOriginTaxAreaID;
  private String productClass;
}