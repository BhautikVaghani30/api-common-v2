/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.annotation;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * This aspect class executes a public reactive method (method with return type as Mono or Flux) in a separate thread pool.
 * Connection pool is defined only once with the given "threadNamePrefix" and then will be reused.
 * Example use :: 
 * {@code @ReactiveThreadPool(threadNamePrefix = "Commercetools-", corePoolSize = 5)}
 * 
 * If threadNamePrefix is not specified then threadNamePrefix = "UltaReactiveThreadPool-", corePoolSize = 200 will be used.
 * 
 * Example use :: 
 * {@code @ReactiveThreadPool}
 * 
 * @author ULTA
 *
 */

@Aspect
@Slf4j
@Component
public class ReactiveThreadPoolAspect {
  

  @Value("${ulta.reactive.default-thread-name-prefix:UltaReactiveThreadPool-}")
  private String defaultThreadNamePrefix;
  
  @Value("${ulta.reactive.default-core-pool-size:200}")
  private int defaultCorePoolSize;
  
  private Map<String, ThreadPoolTaskExecutor> cachedExecutorPool;

    @Around("@annotation(reactiveThreadPool)")
    public Object subscribeOn(ProceedingJoinPoint joinPoint, ReactiveThreadPool reactiveThreadPool) throws Throwable {
      
      Object returnVal = joinPoint.proceed();
      
      MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
      log.debug("Checking return type of the method, must be one of Mono or Flux  :: \"{}\"", methodSignature.getReturnType());
      
      if(Mono.class.isAssignableFrom(methodSignature.getReturnType())) {
        log.debug("Return type found as Mono. Executing subscribeOn Mono.");
        return ((Mono<?>)returnVal).subscribeOn(Schedulers.fromExecutor(getThreadPoolTaskExecutor(reactiveThreadPool)));
      } else if(Flux.class.isAssignableFrom(methodSignature.getReturnType())) {
        log.debug("Return type found as Flux. Executing subscribeOn Flux.");
        return ((Flux<?>)returnVal).subscribeOn(Schedulers.fromExecutor(getThreadPoolTaskExecutor(reactiveThreadPool)));
      } else {
        log.warn("The return type \"{}\" is not supported. Skipping executing custom threadpool.",methodSignature.getReturnType() );
        return returnVal;
      }
    }

    
  private ThreadPoolTaskExecutor getThreadPoolTaskExecutor(ReactiveThreadPool reactiveThreadPool) {

    if (cachedExecutorPool == null) {
      cachedExecutorPool = new ConcurrentHashMap<>();
    }

    String threadNamePrefix;
    int corePoolSize;


    if (reactiveThreadPool != null && StringUtils.hasText(reactiveThreadPool.threadNamePrefix())) {
      threadNamePrefix = reactiveThreadPool.threadNamePrefix();
      corePoolSize =
          reactiveThreadPool.corePoolSize() < 1 ? defaultCorePoolSize : reactiveThreadPool.corePoolSize();
    } else {
      threadNamePrefix = defaultThreadNamePrefix;
      corePoolSize = defaultCorePoolSize;
    }
    
    return cachedExecutorPool.computeIfAbsent(threadNamePrefix, key -> {
      ThreadPoolTaskExecutor extr = new ThreadPoolTaskExecutor();
      extr.setCorePoolSize(corePoolSize);
      extr.setThreadNamePrefix(threadNamePrefix);
      extr.initialize();
      return extr;
    });

  }
    
}