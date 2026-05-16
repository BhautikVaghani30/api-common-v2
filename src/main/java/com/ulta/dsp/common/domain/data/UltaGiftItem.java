/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ulta.dsp.common.model.UltaMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaGiftItem {
  private String lineItemId;
  private String fulfillmentType;
  private UltaFreeGift freeGift;
  private String promoId;
  private String promoValidity;
  private Boolean moveToBag;
  private Boolean indulge;
  @JsonProperty(value="isOnline")
  private boolean isOnline;
  private List<UltaMessage> messages;
  private String hazmatCode;
}