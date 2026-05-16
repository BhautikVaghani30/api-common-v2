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

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UltaAuth0TokenResponse {
  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("expires_in")
  private Long expiresIn;
  @JsonProperty("token_type")
  private String tokenType;
}