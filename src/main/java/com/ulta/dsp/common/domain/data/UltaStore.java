/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.ulta.dsp.common.model.UltaMessage;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UltaStore {
  private String storeId;
  private String distanceInMiles;
  private String timings;
  private String sla;
  private UltaStoreAddress storeContactInfo;
  private String sddDeliveryFee;
  private String curbsidePickupHours;
  private String storeSpecialOpeningHours;
  private String pifTaxDisclaimer;
  private boolean openNow;
  private List<UltaMessage> storeStatus;
  private boolean sddEnabled;
  private boolean pickupEnabled;
  private boolean curbsidePickupEnabled;
}