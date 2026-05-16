/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data;

import com.ulta.dsp.common.domain.corepaymentclient.dto.UltaTokenizedCardDto;
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
public class UltaPaymentDetails extends UltaTokenizedCardDto {

  private boolean primary;

  private UltaAddress address;
}