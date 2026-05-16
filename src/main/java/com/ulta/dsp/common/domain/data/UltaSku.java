/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaSku {

  private String id;
  private Integer maxQuantity;
  private String displayName;
  private UltaImage images;
  private UltaSKUPrice prices;
  private String variantName;
  private String type;
  private UltaBrand brand;
  private UltaDimensions dimensions;
  private boolean gwp;
  private String gwpSubtype;
  private String hazmatCode;
  private String dotComSellable;
  private boolean skuLiveStatus;
  private String shadeDescription;
  private boolean pickUpEligible;
  private String division;
  private String department;
  private String skuClass;
  private String skuSubClass;
  private String taxCategory;
  private String productUrl;
  private boolean cannabis;
  private String mchCategoryId;
  private String mchCategoryName;
  private String mchSegmentId;
  private String mchSegmentName;
  private String mchSubSegmentId;
  private String mchSubSegmentName;
  private String pricingCategory;
  @JsonProperty("isSubscriptionEligible")
  private boolean isSubscriptionEligible;
  private String miraklProductId; //marketplace attribute
  private String sellerName;  //marketplace attribute
  private String sellerId;  //marketplace attribute
  private String variantGroupCode;  //marketplace attribute
  private String offerId; //marketplace attribute
}