/*
 * Copyright (C) 2026 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.corepaymentclient.data.klarna;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.data.UltaField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaKlarnaPayerInfo {
  private String authorizationToken;
  private String authorizationTimeStamp;
  private String klarnaSessionId;
  private UltaField firstName;
  private UltaField lastName;
  private UltaField phone;
  private UltaField email;
  private String paymentType;
  private UltaAddress billingAddress;
  private String countryCode;
  private String authorizedForMid;
}