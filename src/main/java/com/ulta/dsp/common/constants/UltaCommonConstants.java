/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.constants;

import com.ulta.dsp.common.model.UltaCountry;
import com.ulta.dsp.common.model.UltaHeaderKeys;
import com.ulta.dsp.common.model.UltaQueryParamKeys;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class UltaCommonConstants {

  public static final String MDC_KEY_DIGITAL_ID = "digitalId";
  public static final String MDC_KEY_SESSION_ID = "sessionId";
  public static final String MDC_KEY_CORRELATION_ID = "correlationId";
  public static final String COR_UNIQUEID = "UniqueId";

  private UltaCommonConstants() {}

  public static final String REQUEST_CONTEXT_NAME = "ulta-request-context";
  public static final String DEFAULT_COUNTRY_LOCALE = "en-US";
  public static final String HEADERS_CONTEXT_NAME = "httpHeaders";
  public static final String NAMESPACE = "namespace";
  public static final String CLUSTER = "cluster";
  public static final String EMAIL = "email";
  public static final Long  DEFAULT_IDLE_AND_LIFE_TIME = -1L;
  public static final String  METRICS_METHOD = "method";
  public static final String  METRICS_CLASS = "class";
  public static final String  FLAG_NAME = "flagName";
  public static final String  THIRD_PARTY_NAME = "thirdPartyName";


  // custom metrics-constants
  public static final String  METRICS_CUSTOM_RESPONSE_TIME = "custom.service.execution.time";
  public static final String  METRICS_CUSTOM_RESPONSE_TIME_OUT_TOTAL = "custom.service.timeout.total";
  public static final String  METRICS_LONG_RUNNING_THREAD_DURATION_SECONDS = "custom.long.running.thread.duration.seconds";
  public static final String  METRICS_LONG_RUNNING_THREAD_EXCEED_TOTAL = "custom.long.running.thread.exceeded.total";
  public static final String METRICS_EXTERNAL_SYSTEM_EXECUTION_TIME =
      "external.system.execution.time";
 // LD Flags names for custom-metrics enablement/disablement
  public static final String LD_ENABLE_CUSTOM_METRICS_EXECUTION_TIME_FOR_EXTERNAL_CALL_FLAG = "enableCustomMetricsExecutionTimeForExternalCall";
  public static final String LD_ENABLE_CUSTOM_METRICS_RESPONSE_TIME_FLAG = "enableCustomMetricsResponseTime";
  public static final String LD_ENABLE_CUSTOM_METRICS_RESPONSE_TIMEOUT_FLAG = "enableCustomMetricsResponseTimeout";
  public static final String LD_ENABLE_CUSTOM_METRICS_LONG_RUNNING_THREAD_FLAG = "enableCustomMetricsLongRunningThread";
  public static final String ON = "on";
  public static final String OFF = "off";
  public static final String CONTROL = "control";

// LD Flags for Correlation ID
public static final String LD_ENABLE_CORRELATION_ID_FLAG = "enableCorrelationId";

  public static final List<UltaCountry> ULTA_CONTEXT_SUPPORTED_COUNTRIES =
      Collections.unmodifiableList(Arrays.asList(UltaCountry.values()));

  public static final List<String> ULTA_HEADER_CONTEXT_PROPERTIES =
      Collections.unmodifiableList(
          Arrays.asList(
              UltaHeaderKeys.X_ULTA_COUNTRY.getValue(),
              UltaHeaderKeys.X_ULTA_LOCALE.getValue(),
              UltaHeaderKeys.X_ULTA_USER_ID.getValue(),
              UltaHeaderKeys.X_ULTA_SESSION_ID.getValue(),
              UltaHeaderKeys.X_ULTA_ASSOCIATE_ID.getValue(),
              UltaHeaderKeys.X_ULTA_STORE_ID.getValue(),
              UltaHeaderKeys.X_ULTA_CHANNEL.getValue(),
              UltaHeaderKeys.X_ULTA_DEVICE.getValue(),
              UltaHeaderKeys.X_ULTA_AUTH_STATUS.getValue(),
              UltaHeaderKeys.X_ULTA_TRUE_CLIENT_IP.getValue(),
              UltaHeaderKeys.X_AKAMAI_DEVICE_CHARACTERISTICS.getValue(),
              UltaHeaderKeys.X_ULTA_LOYALTY_MEMBER_ID.getValue(),
              UltaHeaderKeys.X_ULTA_LOYALTY_MEMBER_STATUS.getValue(),
              UltaHeaderKeys.X_ULTA_CORRELATION_ID.getValue()));

  public static final List<String> ULTA_QUERYPARAM_CONTEXT_PROPERTIES =
      Collections.unmodifiableList(
          Arrays.asList(
              UltaQueryParamKeys.X_ULTA_COUNTRY.getValue(),
              UltaQueryParamKeys.X_ULTA_LOCALE.getValue(),
              UltaQueryParamKeys.X_ULTA_DELIVERY_METHOD.getValue(),
              UltaQueryParamKeys.X_ULTA_CHANNEL.getValue(),
              UltaQueryParamKeys.X_ULTA_DEVICE.getValue(),
              UltaQueryParamKeys.X_ULTA_STORE_ID.getValue(),
              UltaQueryParamKeys.X_ULTA_ASSOCIATE_ID.getValue(),
              UltaQueryParamKeys.X_ULTA_TRUE_CLIENT_IP.getValue(),
              UltaQueryParamKeys.X_AKAMAI_DEVICE_CHARACTERISTICS.getValue(),
              UltaQueryParamKeys.X_ULTA_CORRELATION_ID.getValue()));
}