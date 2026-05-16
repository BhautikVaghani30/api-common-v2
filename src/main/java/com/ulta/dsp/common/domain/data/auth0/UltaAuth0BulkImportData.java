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
public class UltaAuth0BulkImportData {
  private String type;
  private String status;
  @JsonProperty("connection_id")
  private String connectionId;
  private String connection;
  @JsonProperty("created_at")
  private String createdAt;
  private String id;
}