/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.data.auth0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UltaAuth0JobStatusData {
  private String type;
  private String status;

  @JsonProperty("connection_id")
  private String connectionId;

  private UltaAuth0Summary summary;
  private String connection;

  @JsonProperty("created_at")
  private String createdAt;

  private String id;
}