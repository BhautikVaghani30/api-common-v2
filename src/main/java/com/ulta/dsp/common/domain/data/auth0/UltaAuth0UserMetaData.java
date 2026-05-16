/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data.auth0;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UltaAuth0UserMetaData {

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private String phoneNumber;

    @JsonProperty("phone_verified")
    private Boolean isPhoneVerified;
}