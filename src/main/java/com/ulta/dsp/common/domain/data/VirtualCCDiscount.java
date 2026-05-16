/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;
import com.ulta.dsp.common.model.UltaMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import java.util.List;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VirtualCCDiscount {

  private Long centAmount;
  private String currencyCode;
  private List<UltaMessage> messages;

}