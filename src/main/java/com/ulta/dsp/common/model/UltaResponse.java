/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import lombok.*;

import java.util.List;

/**
 * This class contains information about the requests traversal within the Digital Store Platform
 *
 * <p>
 */
@Data
@Setter(AccessLevel.NONE)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UltaResponse<T> {
  /** {@link UltaMeta} */
  private UltaMeta meta;

  private T data;

  /** List of {@link UltaMessage} */
  private List<UltaMessage> messages;
}