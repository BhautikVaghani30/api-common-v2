/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaMessage {

  private String type;
  private String code;
  private List<UltaMessageToken> tokens;

  public enum UltaMessageType {
    INFO("info"),
    ERROR("error"),
    WARNING("warning");
    private String value;
    private static final Map<String, UltaMessageType> MESSAGE_TYPE_MAP = new HashMap<>();

    UltaMessageType(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    static {
      for (UltaMessageType c : values()) {
        MESSAGE_TYPE_MAP.put(c.value, c);
      }
    }
  }
}