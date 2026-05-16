/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Represents the means by which a cart's items will be delivered to the customer.
 * This is also referred to sometimes as fulfillmentType, but that particular field
 * is used to represent this concept on an individual line item.
 */
@Getter
@AllArgsConstructor
public enum UltaSplitCartDeliveryMethod {
  SHP("SHP"), // ship-to-home
  SDD("SDD"), // same-day delivery (aka Store2Door)
  PICK("PICK"), // in-store pickup (aka BOPIS, buy-online-pickup-in-store)
  SPLIT_SHP("SPLIT_SHP"), // a split cart containing SHP & 3P SHP Marketplace items
  SPLIT_SDD_SHP("SPLIT_SDD_SHP"), // a split cart containing SDD & SHP items
  SPLIT_PICK_SHP("SPLIT_PICK_SHP"); // a split cart containing PICK & SHP items

  private final String value;

  public boolean isSplit() {
    return this.equals(SPLIT_PICK_SHP) || this.equals(SPLIT_SDD_SHP) || this.equals(SPLIT_SHP);
  }

  public boolean isStoreBasedDeliveryMethod() {
    if (isSplit()) {
      throw new IllegalStateException("Split delivery methods represent both store-based and ship-to-home values simultaneously.");
    }
    return this.equals(PICK) || this.equals(SDD);
  }

  public List<UltaSplitCartDeliveryMethod> getApplicableDeliveryMethods() {
    return switch (this) {
      case SPLIT_PICK_SHP -> List.of(PICK, SHP);
      case SPLIT_SDD_SHP -> List.of(SDD, SHP);
      case SPLIT_SHP -> List.of(SHP);
      default -> List.of(this);
    };
  }

  public static UltaSplitCartDeliveryMethod createSplitDeliveryMethod(
      UltaSplitCartDeliveryMethod deliveryMethod1, UltaSplitCartDeliveryMethod deliveryMethod2) {
    if (deliveryMethod1.equals(SHP) || deliveryMethod2.equals(SHP)) {
      if (deliveryMethod1.equals(PICK) || deliveryMethod2.equals(PICK)) {
        return SPLIT_PICK_SHP;
      } else if (deliveryMethod1.equals(SDD) || deliveryMethod2.equals(SDD)) {
        return SPLIT_SDD_SHP;
      }
    }
    throw new IllegalArgumentException("Invalid deliveryMethod combination for split cart");
  }
}