/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data.loyalty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ulta.dsp.common.domain.data.GiftNote;
import com.ulta.dsp.common.domain.data.GiftWrap;
import com.ulta.dsp.common.domain.data.UltaPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UltaLoyaltyMemberDetails {
  String memberNumber;
  String memberSince;
  double loyaltyPoints;
  String loyaltyTier;
  double YTDSpend;
  double lastYearYTDSpend;
  boolean isFraud;
}