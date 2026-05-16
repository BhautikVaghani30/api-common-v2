/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data.auth0;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaAuth0UpdateUserDetailsRequest {

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private String email;

    @JsonProperty("email_verified")
    private Boolean isEmailVerified;

    @JsonProperty("user_metadata")
    private UltaAuth0UserMetaData userMetaData;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private String currentPassword;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private String password;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private String name;

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private String nickname;

  @JsonProperty("app_metadata")
  private UltaAuth0AppMetaData appMetaData;
}