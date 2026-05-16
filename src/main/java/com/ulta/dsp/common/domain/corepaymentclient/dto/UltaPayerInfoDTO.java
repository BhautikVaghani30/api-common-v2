/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.corepaymentclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.data.UltaAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Builder
@AllArgsConstructor
public class UltaPayerInfoDTO {
  private String nonce;
  private UltaAddress shippingAddress;
  private String email;
  private String firstName;
  private String lastName;
  private String payerId;
  private String phone;
  private String countryCode;
  private String paymentType;
  private UltaAddress billingAddress;
}