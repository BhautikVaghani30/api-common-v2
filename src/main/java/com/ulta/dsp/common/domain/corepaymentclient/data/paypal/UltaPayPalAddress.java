/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.paypal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder(builderClassName = "Builder")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UltaPayPalAddress {
  private String line1;
  private String line2;
  private String city;
  private String state;
  private String countryCode;
  private String postalCode;
}