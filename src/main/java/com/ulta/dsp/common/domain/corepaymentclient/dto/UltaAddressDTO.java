/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UltaAddressDTO {

  /** firstName for user account */
  private String firstName;

  /** lastName for user account */
  private String lastName;

  /** addressLine1 for user account */
  private String address1;

  /** phoneNumber for user account */
  private String phoneNumber;

  /** addressLine2 for user account */
  private String address2;

  /** country for address */
  private String country;

  /** postalCode for address */
  private String zipCode;

  /** city for address */
  private String city;

  /** state for address */
  private String state;

  /** Address type */
  private String type;

  /** flag to mark address as primary shipping address */
  private boolean primary;

  /** Commerce Tools Id of the address */
  private String id;
}