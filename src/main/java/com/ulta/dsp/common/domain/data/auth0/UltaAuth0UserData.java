/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data.auth0;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ulta.dsp.common.domain.dto.UltaAuth0UserMetaData;
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
public class UltaAuth0UserData {
  @JsonProperty("updated_at")
  private String updatedAt;
  @JsonProperty("user_id")
  private String auth0Id;
  private String email;
  private String picture;
  private List<UltaAuth0UserIdentity> identities;
  @JsonProperty("email_verified")
  private Boolean emailVerified;
  private String name;
  private String nickname;
  @JsonProperty("created_at")
  private String createdAt;
  @JsonProperty("last_password_reset")
  private String lastPasswordReset;
  @JsonProperty("last_ip")
  private String lastIp;
  @JsonProperty("last_login")
  private String lastLogin;
  @JsonProperty("logins_count")
  private String loginsCount;
  @JsonProperty("user_metadata")
  private UltaAuth0UserMetaData ultaAuth0UserMetaData;
}