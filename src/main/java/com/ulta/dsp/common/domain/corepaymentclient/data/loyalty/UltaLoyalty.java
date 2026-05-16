/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.corepaymentclient.data.loyalty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.corepaymentclient.data.UltaBasePaymentWithOrderId;
import com.ulta.dsp.common.model.UltaMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaLoyalty implements UltaBasePaymentWithOrderId {
  private boolean active;
  private UltaAddress billingAddress;
  private UltaRedemptionInfo loyaltyPoints;
  private UltaloyaltyPaymentTender loyaltyPaymentTender;
  private List<UltaMessage> messages;
  private String date;
  private String orderId;
}