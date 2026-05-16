/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.paymentinterfaces.rts.mapper;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ulta.dsp.common.domain.data.UltaCreditCardAuthInfo;
import com.ulta.dsp.paymentinterfaces.utils.UltaPaymentUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.ulta.dsp.paymentinterfaces.constants.UltaPaymentConstants;
import com.ulta.dsp.paymentinterfaces.creditcard.model.response.UltaRTSResponse;
import org.springframework.util.StringUtils;

import static com.ulta.dsp.paymentinterfaces.constants.UltaPaymentConstants.ULTA_AVS_REGEX_PATTERN;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UltaRTSResponseMapper {

  @Mapping(target = "authId", source = "authCode")
  @Mapping(target = "needsReversal", source = "needsReversal")
  @Mapping(target = "avs", source = "options" , qualifiedByName = "getAVS")
  @Mapping(target = "status", source = "actionCode", qualifiedByName = "getStatus")
  UltaCreditCardAuthInfo mapToCreditCardResponse(final UltaRTSResponse ultaRTSResponse);
  final static Map<String, String> statusMap =
          Map.of(
              "0",
              UltaPaymentConstants.AUTHORIZED,
              "1",
              UltaPaymentConstants.DECLINED,
              "2",
              UltaPaymentConstants.DECLINED);

  /**
   * This method is used to populate status
   *
   * @param actionCode
   * @return
   */
  @Named("getStatus")
  default String mapStatus(String actionCode) {
    String status = null;
    if (null != actionCode) {
      status = statusMap.get(actionCode);
    }
    return status;
  }

  @Named("getAVS")
  default String mapAVS(String options) {
    if (!StringUtils.isEmpty(options)) {
      String avs = UltaPaymentUtils.findMatchedPattern(options, ULTA_AVS_REGEX_PATTERN);
      if (!StringUtils.isEmpty(avs)) {
        String filteredAVS = avs.substring(avs.length() - 1);
        return filteredAVS;
      }
      return null;
    }
    return null;
  }
}