/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.annotation;

import com.ulta.dsp.common.config.LaunchDarklyConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Configuration
public class FeatureFlagAspect {

  @Autowired private ApplicationContext appCtx;

  @Autowired private LaunchDarklyConfig launchDarklyConfig;

  @Around("@annotation(feature)")
  public Object flipFeatureAround(ProceedingJoinPoint joinPoint, Feature feature) throws Throwable {
    boolean isFeatureToggled = check(feature);

    if (isFeatureToggled) {
      Object bean = appCtx.getBean(feature.executeBeanIfOn());
      log.debug(
          "Feature {} is turned on and will be using the bean {} ",
          feature.flag(),
          bean.getClass().getSimpleName());
      return execute(bean, joinPoint);
    } else {
      log.debug(
          "Feature {} is NOT On and will continue calling {} ", joinPoint.getTarget().getClass());
      return joinPoint.proceed();
    }
  }

  private Object execute(Object bean, ProceedingJoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    int size = joinPoint.getArgs().length;
    Class args[] = new Class[size];
    for (int idx = 0; idx < size; idx++) {
      args[idx] = joinPoint.getArgs()[idx].getClass();
    }

    try {
      Method method = bean.getClass().getMethod(methodName, args);
      return method.invoke(bean, joinPoint.getArgs());
    } catch (Exception exception) {
      log.error("Exception running method {} with args {}", methodName, joinPoint.getArgs());
      throw new RuntimeException(exception);
    }
  }

  /**
   * Make a call to launch darkly to see if this feature is on or off
   *
   * @param feature
   * @return
   */
  private boolean check(Feature feature) {
    String flagName = feature.flag();
    log.debug("Check LaunchDarkly flag status: {}", flagName);
    return launchDarklyConfig.flagEnabled(flagName);
  }
}