/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Duration;

@Getter
@Setter
public class UltaWebClientProperties {
    private String connectionPoolName="ulta-webclient-conn-pool-"
            + RandomStringUtils.randomAlphanumeric(10);
    private Integer maxConnectionPoolSize = -1;
    private int writeTimeoutMilli=1000;
    private int readTimeOutMilli=1000;
    private boolean compressionEnabled=false;
    private boolean loggingEnabled=false;
    private Long maxIdleTimeMilliSec;
    private Long maxLifeTimeMilliSec;
}