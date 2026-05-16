/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.data.UltaPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaMessageTokenPrice {

  private String key;
  private List<UltaPrice> value;
}