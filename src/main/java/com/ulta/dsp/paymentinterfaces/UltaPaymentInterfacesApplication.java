/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.paymentinterfaces;

import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubReactiveAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.health.PubSubHealthIndicatorAutoConfiguration;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication(
        exclude = {
        GcpPubSubAutoConfiguration.class,
        PubSubHealthIndicatorAutoConfiguration.class,
        GcpPubSubReactiveAutoConfiguration.class
})
@OpenAPIDefinition(
    info =
        @Info(
            title = "Orchestration - Payment Interfaces",
            contact =
                @Contact(
                    name = "Ulta Beauty",
                    url = "https://www.ulta.com",
                    email = "tbd@ulta.com"),
            version = "2.0",
            description = "Payment services for the Digital Store Platform"),
    servers = {
      @Server(
          url = "{protocol}://{env}/{version}",
          variables = {
            @ServerVariable(
                defaultValue = "https",
                name = "protocol",
                allowableValues = {"http", "https"}),
            @ServerVariable(
                defaultValue = "nonprod.ultagcp.lcl",
                name = "env",
                allowableValues = {
                        "nonprod.ultagcp.lcl",
                        "da1.ultainc.com",
                        "services-da1.ultainc.com",
                        "services-da1.ultainc.com/ecom",
                        "qa1.ultainc.com",
                        "services-qa1.ultainc.com"}),
            @ServerVariable(
                defaultValue = "v2",
                name = "version",
                allowableValues = {"v1", "v2"})
          }),
      @Server(
          url = "{protocol}://{namespace}{env}/{version}",
          variables = {
            @ServerVariable(
                defaultValue = "https",
                name = "protocol",
                allowableValues = {"http", "https"}),
            @ServerVariable(defaultValue = "", name = "namespace"),
            @ServerVariable(
                defaultValue = "nonprod.ultagcp.lcl",
                name = "env",
                allowableValues = {"nonprod.ultagcp.lcl", "prod.ultagcp.lcl", "ultainc.lcl"}),
            @ServerVariable(
                defaultValue = "v2",
                name = "version",
                allowableValues = {"v1", "v2"})
          }),
      @Server(
          url = "{protocol}://localhost:{port}/{version}",
          variables = {
            @ServerVariable(
                defaultValue = "http",
                name = "protocol",
                allowableValues = {"http", "https"}),
            @ServerVariable(
                defaultValue = "8080",
                name = "port",
                allowableValues = {
                    "8080", "8081", "8082", "8083", "8084", "8085", "8086", "8087", "8088", "8089", "8090"
                }),
            @ServerVariable(
                defaultValue = "v2",
                name = "version",
                allowableValues = {"v1", "v2"})
          }),
      @Server(
          url = "/{version}",
          variables = {
            @ServerVariable(
                defaultValue = "v2",
                name = "version",
                allowableValues = {"v1", "v2"})
          })
    },
    tags = {
      @Tag(
          name = "Payment Interfaces",
          externalDocs =
              @ExternalDocumentation(
                  url =
                      "https://confluence.ulta.com/display/DSOTF/Orchestration+Paymentinterfaces+-+Afterpay",
                  description = "More @:"),
          description = "Provides features for payment  operations for the Digital Store Platform")
    })
public class UltaPaymentInterfacesApplication {

  /**
   * Main application class for start-up
   *
   * @param args
   */
  public static void main(String[] args) {
    SpringApplication.run(UltaPaymentInterfacesApplication.class);
  }

  @Bean
  @Qualifier("taskScheduler")
  public TaskScheduler taskScheduler() {
    return new ThreadPoolTaskScheduler(); // single threaded by default
  }
}