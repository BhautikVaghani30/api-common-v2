/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.config;

import com.ulta.dsp.common.webclient.UltaWebClientBuilderFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"com.ulta.dsp.common"})
@Configuration
public class UltaWebClientBuilderConfiguration {

  @Bean("ultaWebClientBuilderFactory")
  public FactoryBean<?> serviceLocatorFactoryBean() {
    ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
    factoryBean.setServiceLocatorInterface(UltaWebClientBuilderFactory.class);
    return factoryBean;
  }
}