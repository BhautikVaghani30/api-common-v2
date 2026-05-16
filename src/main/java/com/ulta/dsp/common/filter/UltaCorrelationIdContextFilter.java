package com.ulta.dsp.common.filter;

import com.ulta.dsp.common.config.LaunchDarklyConfig;
import com.ulta.dsp.common.utils.ContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static com.ulta.dsp.common.constants.UltaCommonConstants.LD_ENABLE_CORRELATION_ID_FLAG;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class UltaCorrelationIdContextFilter implements WebFilter {
  private LaunchDarklyConfig launchDarklyConfig;

  public UltaCorrelationIdContextFilter(LaunchDarklyConfig launchDarklyConfig) {
    this.launchDarklyConfig = launchDarklyConfig;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    boolean ldFlagEnabled = false; // Default to false if no flag is specified
    ldFlagEnabled = launchDarklyConfig.flagEnabled(LD_ENABLE_CORRELATION_ID_FLAG);
    boolean finalLdFlagEnabled = ldFlagEnabled;
    return chain
        .filter(exchange)
        .contextWrite(
            context -> {
              if (finalLdFlagEnabled) {
                context = ContextUtils.setCorrelationIdWithContext(context);
              }
              return context;
            });
  }
}