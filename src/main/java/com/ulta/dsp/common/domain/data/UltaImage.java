/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.domain.data;


import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UltaImage {
  private UltaAsset mainImage;

  public enum UltaImageType {
    IMAGE("image"),
    VIDEO("video"),
    DOCUMENT("document");

    public final String value;
    private static final Map<String, UltaImageType> MESSAGE_TYPE_MAP = new HashMap<>();

    UltaImageType(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    static {
      for (UltaImageType c : values()) {
        MESSAGE_TYPE_MAP.put(c.value, c);
      }
    }
  }
}