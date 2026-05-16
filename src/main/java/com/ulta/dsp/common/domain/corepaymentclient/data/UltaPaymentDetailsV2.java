/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.domain.corepaymentclient.data;

import com.ulta.dsp.common.domain.corepaymentclient.dto.UltaTokenizedCardDtoV2;
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
public class UltaPaymentDetailsV2 extends UltaTokenizedCardDtoV2 {

  private boolean primary;

  private UltaAddress address;

  private UltaField expirationYear;

  private UltaField expirationMonth;
}