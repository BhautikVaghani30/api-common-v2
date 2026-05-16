/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.validation.UltaValidAddressField;
import lombok.*;

import static com.ulta.dsp.common.validation.UltaValidationConstants.*;

/** this bean contains store address information using UltaField object */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UltaStoreAddress {

  @UltaValidAddressField(regex = ALPHANUMERIC, message = "{INVALID_ADDRESS1}")
  private UltaField address1;

  private UltaField address2;

  @UltaValidAddressField(regex = ALPHANUMERIC, message = "{INVALID_CITY}")
  private UltaField city;

  @UltaValidAddressField(regex = PROVINCIAL, message = "{INVALID_PROVINCIAL}")
  private UltaField state;

  private UltaField country;

  @UltaValidAddressField(regex = POSTALCODE, message = "{INVALID_ZIPCODE}")
  private UltaField zipCode;

  @UltaValidAddressField(regex = PHONENO, message = "{INVALID_PHONENO}")
  private UltaField phoneNumber;
  private UltaField displayName;
  private UltaField latitude;
  private UltaField longitude;
}