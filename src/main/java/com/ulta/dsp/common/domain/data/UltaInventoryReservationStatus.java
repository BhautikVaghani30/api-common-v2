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
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UltaInventoryReservationStatus {
  private String itemId;
  private String deliveryMethod;
  private String fulfillmentType;
  private BigDecimal requiredQty;
  private BigDecimal availableQty;
  private BigDecimal totalReservedQty;

  @Deprecated
  private UltaInventoryReservationDetails reservationDetails;
  private List<UltaInventoryReservationDetails> reservationDetailsList;
  private List<UltaMessage> messages;

}