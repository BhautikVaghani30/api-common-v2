/*
 * Copyright (C) 2019 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.buy;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication
@ComponentScan({"com.ulta.dsp.dav.client", "com.ulta.dsp.buy", "com.ulta.dsp.buy.checkout.config"})
@OpenAPIDefinition(
    info =
        @Info(
            title = "Ulta Checkout Orchestration Service",
            contact =
                @Contact(
                    name = "Ulta Beauty",
                    url = "https://www.ulta.com",
                    email = "tbd@ulta.com"),
            version = "2.0",
            description = "Checkout Operations Orchestration Service for Digital Store Platform"),
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
                  "da1.ultainc.com",
                  "services-da1.ultainc.com",
                  "services-da1.ultainc.com/ecom",
                  "qa1.ultainc.com",
                  "services-qa1.ultainc.com",
                  "services-qa1.ultainc.com/ecom"
                }),
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
                  "8080", "8081", "8082", "8083", "8084", "8085", "8086", "8087", "8088", "8089",
                  "8090"
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
                allowableValues = {"v2"})
          }),
      @Server(
          url = "{protocol}://{custom}",
          variables = {
            @ServerVariable(
                defaultValue = "https",
                name = "protocol",
                allowableValues = {"http", "https"}),
            @ServerVariable(defaultValue = "", name = "custom")
          })
    },
    tags = {
      @Tag(
          name = "Checkout",
          externalDocs =
              @ExternalDocumentation(
                  url = "https.ulta.atlassian.net/wiki/spaces/DSOTF/pages/24748275/Checkout",
                  description = "More @:"),
          description = "Provides the Checkout operations for Digital Store Platform")
    })
public class UltaCheckoutApplication {
  public static void main(String[] args) {
    SpringApplication.run(UltaCheckoutApplication.class);
  }

  /**
   * Initialize message resource
   *
   * @return ResourceBundleMessageSource
   */
  @Bean
  public ResourceBundleMessageSource messageSource() {

    ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
    rs.setBasename("message");
    rs.setDefaultEncoding("UTF-8");
    rs.setUseCodeAsDefaultMessage(true);
    return rs;
  }

  /**
   * associating validator with message resource
   *
   * @return LocalValidatorFactoryBean
   */
  @Bean
  public LocalValidatorFactoryBean validator() {
    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(messageSource());
    return bean;
  }
}