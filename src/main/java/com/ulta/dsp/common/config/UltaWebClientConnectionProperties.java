/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.config;

import com.ulta.dsp.common.model.UltaWebClientProperties;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
Use below pattern to set the webclient config properties
for "promotion" client

ulta.webclient.properties.promotion.maxConnectionPoolSize=100

for "price" client

ulta.webclient.properties.price.maxConnectionPoolSize=200


 */
@ConfigurationProperties(prefix = "ulta.webclient")
@Getter
@Setter
@Component
public class UltaWebClientConnectionProperties {
    Map<String, UltaWebClientProperties> properties;

}