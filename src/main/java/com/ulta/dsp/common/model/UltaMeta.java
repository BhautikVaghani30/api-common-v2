/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

/**
 * This class contains additional information about the requests traversal within the Digital Store
 * Platform
 *
 * <p>
 */
@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class UltaMeta {

  /** Timestamp of when the response exits the service */
  @Builder.Default private String timestamp = Instant.now().toString();
  /** Status of the service call, can be either success, fail, or partial */
  private String status;

  /** updatedResources to indicate the side effects of transactions */
  private List<UltaUpdatedResource> updatedResources;
}