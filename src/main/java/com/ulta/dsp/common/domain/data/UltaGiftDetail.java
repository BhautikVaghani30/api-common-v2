/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UltaGiftDetail {

  private String lineItemId;

  private String productKey;

  private UltaPrice price;

  private String skuId;

  private GiftWrap giftWrap;

  private GiftNote giftNote;

  private Boolean giftEligible;
}