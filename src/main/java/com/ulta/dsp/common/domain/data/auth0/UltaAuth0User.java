/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data.auth0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UltaAuth0User {
  private String email;
  private String connection;
  @JsonProperty("custom_password_hash")
  private UltaAuth0CustomPasswordHash customPasswordHash;
  @JsonProperty("user_metadata")
  private UltaAuth0UserMetaData userMetadata;
  @JsonProperty("app_metadata")
  private Object appMetadata;
  @JsonProperty("mfa_factors")
  private List<MfaFactor> mfaFactors;
}