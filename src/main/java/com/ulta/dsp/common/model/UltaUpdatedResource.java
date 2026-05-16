/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class UltaUpdatedResource {

  
  /** Updated resource type value */
  private UltaUpdatedResourceType rel;
  
  /** Reference link to updated resource */
  private String href;    
  
}