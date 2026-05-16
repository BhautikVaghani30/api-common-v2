/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data.auth0;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UltaAuth0BulkImportRequest {
    private String email;
    private String connection;
    @JsonProperty("user_metadata")
    private UltaAuth0UserMetaData ultaAuth0UserMetaData;
    @JsonProperty("app_metadata")
    private Map<String, Object> appMetaData;
    @JsonProperty("mfa_factors")
    private List<MfaFactor> mfaFactors;
}