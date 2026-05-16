/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaSKUDetail {

  private String id;
  private String dotComSellable;
  private Integer maxQuantity;
  private boolean live;
  private String startDate;
  private String endDate;
  private String comingSoonStartDate;
  private String comingSoonEndDate;
  private boolean s2dEnabled;
  private boolean gwp;
  private String gwpSubtype;
  @JsonProperty("isSubscriptionEligible")
  private boolean isSubscriptionEligible;
  private List<String> subscriptionFrequencyOptions;
  private String subType; // used for marketplace identification
}