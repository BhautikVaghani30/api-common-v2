/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.venmo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.domain.data.UltaAddress;
import com.ulta.dsp.common.domain.data.UltaField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaVenmoPayerInfo {

  private String nonce;
  private String userName;
  private UltaField firstName;
  private UltaField lastName;
  private UltaField phone;
  private UltaField email;
  private String paymentType;
  private UltaAddress shippingAddress;
  private UltaAddress billingAddress;
  private String countryCode;
}