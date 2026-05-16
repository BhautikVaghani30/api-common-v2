/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(builderClassName = "Builder")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UltaDMResponse {
  private String score;
  private String status;
  private String submitTime;
  private String reason;
  private String dmTransactionId;
}