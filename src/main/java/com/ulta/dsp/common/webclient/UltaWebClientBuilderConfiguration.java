/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.webclient;

import com.ulta.dsp.common.config.UltaWebClientConnectionProperties;
import com.ulta.dsp.common.constants.UltaWebClientBuilderTypeConstants;
import com.ulta.dsp.common.model.UltaWebClientProperties;
import com.ulta.dsp.common.webclient.filter.UltaHeaderExchange;
import com.ulta.dsp.common.webclient.filter.UltaRequestContextToQueryParamExchange;
import com.ulta.dsp.common.webclient.filter.UltaWebClientRequestResponseLogger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import static com.ulta.dsp.common.constants.UltaCommonConstants.DEFAULT_IDLE_AND_LIFE_TIME;

@Slf4j
@Configuration
public class UltaWebClientBuilderConfiguration {

  @Autowired
  UltaWebClientConnectionProperties ultaWebClientConnectionProperties;

  @Value("${ulta.webclient.config.writetimeout:10000}")
  private Integer writeTimeoutInMilliSec;

  @Value("${ulta.webclient.config.readtimeout:10000}")
  private Integer readTimeoutInMilliSec;


  @Value("${ulta.logging.requestresponse.enabled:false}")
  private boolean loggingEnabled;

  @Value("${ulta.webclient.maxConnections:-1}")
  private Integer maxConnections;

  @Value("${ulta.webclient.config.maxIdleTimeInMilliSec:-1}")
  private Long maxIdleTimeInMilliSec;

  @Value("${ulta.webclient.config.maxLifeTimeInMilliSec:-1}")
  private Long maxLifeTimeInMilliSec;

  public UltaWebClientProperties createDefaultWebClientConfig() {
    UltaWebClientProperties ultaWebClientProperties = new UltaWebClientProperties();
    ultaWebClientProperties.setCompressionEnabled(false);
    ultaWebClientProperties.setConnectionPoolName("ulta-webclient-conn-pool");
    ultaWebClientProperties.setMaxConnectionPoolSize(maxConnections);
    ultaWebClientProperties.setReadTimeOutMilli(readTimeoutInMilliSec);
    ultaWebClientProperties.setWriteTimeoutMilli(writeTimeoutInMilliSec);
    ultaWebClientProperties.setLoggingEnabled(loggingEnabled);
    ultaWebClientProperties.setMaxIdleTimeMilliSec(maxIdleTimeInMilliSec);
    ultaWebClientProperties.setMaxLifeTimeMilliSec(maxLifeTimeInMilliSec);
    return ultaWebClientProperties;
  }

  @Bean(name = "defaultWebClientConfig")
  public String defaultWebClientConfig(){
    return "defaultWebClientConfig";
  }


  @Bean(UltaWebClientBuilderTypeConstants.CORE_WEB_CLIENT_BUILDER)
  @Scope("prototype")
  public WebClient.Builder createCoreBuilder(
          @Qualifier("defaultWebClientConfig") String webClientConfigName) {

    return createDefaultBuilder(getWebClientConfigProperties(webClientConfigName))
        .filter(new UltaHeaderExchange())
        .filter(new UltaRequestContextToQueryParamExchange());
  }

  @Bean(UltaWebClientBuilderTypeConstants.ORCH_WEB_CLIENT_BUILDER)
  @Scope("prototype")
  public WebClient.Builder createOrchestrationBuilder(
          @Qualifier("defaultWebClientConfig") String webClientConfigName) {

    return createDefaultBuilder(getWebClientConfigProperties(webClientConfigName))
            .filter(new UltaHeaderExchange());
  }
  
  @Bean(UltaWebClientBuilderTypeConstants.ORCH_WITH_COMPRESSION_WEB_CLIENT_BUILDER)
  @Scope("prototype")
  public WebClient.Builder createOrchestrationWithCompressionBuilder(
          @Qualifier("defaultWebClientConfig") String webClientConfigName) {
    UltaWebClientProperties defaultWebClientConfig = getWebClientConfigProperties(webClientConfigName);
    defaultWebClientConfig.setCompressionEnabled(true);
    return createDefaultBuilder(defaultWebClientConfig).filter(new UltaHeaderExchange());
  }

  @Bean(UltaWebClientBuilderTypeConstants.EXTERNAL_WEB_CLIENT_BUILDER)
  @Scope("prototype")
  public WebClient.Builder createExternalBuilder(
          @Qualifier("defaultWebClientConfig") String webClientConfigName) {

    return createDefaultBuilder(getWebClientConfigProperties(webClientConfigName));
  }

  private UltaWebClientProperties getWebClientConfigProperties(String webClientConfigName){
   return  Optional.ofNullable(ultaWebClientConnectionProperties.getProperties()).
            map(properties ->properties.getOrDefault(webClientConfigName,createDefaultWebClientConfig()))
           .orElse(createDefaultWebClientConfig());
  }

  private WebClient.Builder createDefaultBuilder(UltaWebClientProperties properties) {

    // TODO: Performance check for doOnConnected vs doOnRequest
    HttpClient httpClient =
        HttpClient.create(createConnectionProvider(properties))
            .doOnConnected(
                connection ->
                    connection.addHandlerLast(
                        new WriteTimeoutHandler(
                            properties.getWriteTimeoutMilli(), TimeUnit.MILLISECONDS)))
            .doOnConnected(
                connection ->
                    connection.addHandlerLast(
                        new ReadTimeoutHandler(
                            properties.getReadTimeOutMilli(), TimeUnit.MILLISECONDS)))
            .doOnRequest(
                (request, connection) ->
                    connection.addHandlerFirst(new CustomLogger(HttpClient.class)))
            .wiretap(properties.isLoggingEnabled())
            .compress(properties.isCompressionEnabled());
    return WebClient
            .builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filter(new UltaWebClientRequestResponseLogger(properties.isLoggingEnabled()));

  }

  public static class CustomLogger extends LoggingHandler {
    public CustomLogger(Class<?> clazz) {
      super(clazz);
    }

    @Override
    protected String format(ChannelHandlerContext ctx, String event, Object arg) {
      if (arg instanceof ByteBuf) {
        ByteBuf msg = (ByteBuf) arg;
        return msg.toString(Charset.forName("utf-8"));
      }
      return super.format(ctx, event, arg);
    }
  }

  protected ConnectionProvider createConnectionProvider(UltaWebClientProperties ultaWebClientProperties){

    Integer maxConnectionsLocal = Optional.ofNullable(ultaWebClientProperties.getMaxConnectionPoolSize()).filter(val -> val != -1)
            .orElse(ConnectionProvider.DEFAULT_POOL_MAX_CONNECTIONS);
    ConnectionProvider.Builder builder = ConnectionProvider.builder(ultaWebClientProperties.getConnectionPoolName())
            .maxConnections(maxConnectionsLocal)
            .metrics(true);

    Optional.ofNullable(ultaWebClientProperties.getMaxIdleTimeMilliSec())
            .filter(val -> val != DEFAULT_IDLE_AND_LIFE_TIME)
            .ifPresent(val -> builder.maxIdleTime(Duration.ofMillis(val)));

    Optional.ofNullable(ultaWebClientProperties.getMaxLifeTimeMilliSec())
            .filter(val -> val != DEFAULT_IDLE_AND_LIFE_TIME)
            .ifPresent(val -> builder.maxLifeTime(Duration.ofMillis(val)));

    if (log.isDebugEnabled()) {
      log.debug("UltaWebClientBuilderConfiguration::createConnectionProvider - maxConnection : {}, "
              + "maxIdleTimeMillis : {}, maxLifeTimeMillis : {}", String.valueOf(maxConnectionsLocal), String.valueOf(ultaWebClientProperties.getMaxIdleTimeMilliSec()), String.valueOf(ultaWebClientProperties.getMaxLifeTimeMilliSec()));
    }

    return builder.build();
  }
}