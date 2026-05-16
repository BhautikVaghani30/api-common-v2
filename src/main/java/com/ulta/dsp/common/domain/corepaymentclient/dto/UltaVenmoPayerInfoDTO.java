/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.corepaymentclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.data.UltaAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UltaVenmoPayerInfoDTO {
  private String nonce;
  private UltaAddress shippingAddress;
  private String email;
  private String firstName;
  private String lastName;
  private String userName;
  private String phone;
  private String countryCode;
  private String paymentType;
  private UltaAddress billingAddress;
}