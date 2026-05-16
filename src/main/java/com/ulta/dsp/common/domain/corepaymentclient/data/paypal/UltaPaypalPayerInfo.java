/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.paypal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.data.UltaField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaPaypalPayerInfo {

  private String nonce;
  private String payerId;
  private UltaField firstName;
  private UltaField lastName;
  private UltaField phone;
  private UltaField email;
  private String paymentType;
  private UltaAddress shippingAddress;
  private UltaAddress billingAddress;
  private String countryCode;
}