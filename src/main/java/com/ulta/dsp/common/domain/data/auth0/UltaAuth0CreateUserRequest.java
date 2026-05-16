/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data.auth0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class UltaAuth0CreateUserRequest {
  private String email;
  private String name = "defaultname";
  private String nickname = "defaultnickname";
  @JsonProperty("user_id")
  private String auth0Id;
  private String password;
  private String connection;
  @JsonProperty("user_metadata")
  private UltaAuth0UserMetaData userMetaData;
}