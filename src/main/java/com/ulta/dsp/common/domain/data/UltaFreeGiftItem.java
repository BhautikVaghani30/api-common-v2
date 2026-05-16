/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UltaFreeGiftItem {
  private String giftShippingRestriction;
  private String giftProductId;
  private String giftPDPUrl;
  private String giftSkuId;
  private String giftDisplayName;
  private UltaPrice listPrice;
  private String giftCategoryName;
  private String giftImageURL;
  private Boolean selected;
  private String giftBrandName;
  private String variantName;
  private String type;
  private Integer quantity;
  private UltaInventoryReservationStatus inventoryReservationStatus;
}