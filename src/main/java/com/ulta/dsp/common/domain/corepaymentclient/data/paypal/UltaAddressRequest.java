/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data.paypal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UltaAddressRequest {

  private String address1;

  private String address2;

  private String city;

  private String state;

  private String country;

  private String zipCode;

  private String additionalAddressInfo;

}