/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ulta.dsp.common.model.UltaMessage;
import com.ulta.dsp.common.validation.UltaValidAddressField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.ulta.dsp.common.validation.UltaValidationConstants.ALPHANUMERIC;
import static com.ulta.dsp.common.validation.UltaValidationConstants.PHONENO;
import static com.ulta.dsp.common.validation.UltaValidationConstants.POSTALCODE;
import static com.ulta.dsp.common.validation.UltaValidationConstants.PROVINCIAL;

/** this bean contains profile address information using UltaField object */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UltaAddress {

  @UltaValidAddressField(regex = ALPHANUMERIC, message = "{INVALID_FIRST_NAME}")
  private UltaField firstName;

  @UltaValidAddressField(regex = ALPHANUMERIC, message = "{INVALID_LAST_NAME}")
  private UltaField lastName;

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

  // @ValidAddressField(regex = RegexValidation.EMAIL, message = "{ERROR_EMAIL}")
  private UltaField email;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private UltaField additionalAddressInfo;

  private UltaMessage message;

  private String entityId;
  private String id;
  private boolean primary;
  private String type;

  private String taxId;
  private boolean addressVerified;
  private String verifiedDate;
  private UltaField displayName;
  private UltaField latitude;
  private UltaField longitude;
  private String shippingStatus;
  private String verificationStatus;
  private boolean emailOptIn;

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  @JsonProperty("isRestrictedAddress")
  private boolean isRestrictedAddress;
}