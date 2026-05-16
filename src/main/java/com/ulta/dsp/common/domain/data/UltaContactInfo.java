/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import static com.ulta.dsp.common.validation.UltaValidationConstants.ALPHANUMERIC;
import static com.ulta.dsp.common.validation.UltaValidationConstants.PHONENO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ulta.dsp.common.validation.UltaValidAddressField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** this bean contains contact info information using UltaField object */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UltaContactInfo {

  @UltaValidAddressField(regex = ALPHANUMERIC, message = "{INVALID_FIRST_NAME}")
  private UltaField firstName;

  @UltaValidAddressField(regex = ALPHANUMERIC, message = "{INVALID_LAST_NAME}")
  private UltaField lastName;

  @UltaValidAddressField(regex = PHONENO, message = "{INVALID_PHONENO}")
  private UltaField phoneNumber;

  private UltaField email;
}