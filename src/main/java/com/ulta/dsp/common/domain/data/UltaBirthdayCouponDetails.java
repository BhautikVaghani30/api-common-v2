/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.model.UltaMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaBirthdayCouponDetails {
  private String birthdayCouponCode;
  private String birthdayCouponStatus;
  private String promotionId;
  private Integer birthdayMonth;
  private Integer redemptionYear;
  private List<UltaMessage> messages;
}