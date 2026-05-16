/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ulta.dsp.common.model.UltaMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** UltaField class define field response data structure using value & message attributes */
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UltaField {
  private String value;
  private List<UltaMessage> messages;
}