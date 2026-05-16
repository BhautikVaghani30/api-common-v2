/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
  private String firstName;
  private String lastName;
  private String country;
  private String phoneNumber;
  private String address2;
  private String city;
  private String address1;
  private String postalCode;
  private String state;
  private String email;
}