/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;

@Configuration
@ConditionalOnClass(WebFluxConfigurer.class)
@AutoConfigureBefore(WebFluxAutoConfiguration.class)
@EnableConfigurationProperties({ServerProperties.class})
public class UltaApplicationContextPathConfig {

  private final ServerProperties serverProperties;

  public UltaApplicationContextPathConfig(ServerProperties serverProperties) {
    this.serverProperties = serverProperties;
  }

  @Bean
  @ConditionalOnProperty(
      name = {"spring.webflux.base-path"},
      matchIfMissing = true)
  public WebFilter ulatContextPathWebFilter() {
    String contextPath = serverProperties.getServlet().getContextPath();
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      if (!StringUtils.isBlank(contextPath) && request.getURI().getPath().startsWith(contextPath)) {
        return chain.filter(
            exchange.mutate().request(request.mutate().contextPath(contextPath).build()).build());
      }
      return chain.filter(exchange);
    };
  }
}