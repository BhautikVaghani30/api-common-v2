/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.model.UltaMessage;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaLineItem {
  private String id;
  private String productId;
  private Integer quantity;
  private String deliveryMethod;
  private String fulfillmentType;
  private String subType; // used for marketplace identification
  private String productSlug;
  private UltaMessage unitPriceMessage;
  private UltaSku sku;
  private UltaTaxAttributes tax;
  private UltaMessage message;
  private List<UltaAppliedPromotion> appliedPromotions;
  private List<UltaAppliedPromotion> orderLevelPromotionSplit;
  private List<ProratedPromotionDetail> proratedPromotionDetails;
  private List<AdbugMessage> adbugMessageList;
  private List<UltaFreeGiftSku> applicableFreeSkus;
  private String storeId;
  private Integer availableQuantity;
  private String availabilityStatus;
  private UltaInventoryReservationStatus inventoryReservationStatus;
  private UltaProrationDetail prorationDetail;
  private UltaLoyaltyPromotionSplit loyaltyPromotionSplit;
  private Boolean isSflEligible;
  private String vertexProductClass;
  private Boolean couponExcluded;
  private UltaSubscription subscription;
  private boolean onlineOnly;
  private Boolean birthdayCouponApplied;
}