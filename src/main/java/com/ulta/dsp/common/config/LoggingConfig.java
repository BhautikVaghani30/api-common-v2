/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.config;

import ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Aspect
@Configuration
@Slf4j
public class LoggingConfig {

    private final String MDC_CONTEXT_REACTOR_KEY = LoggingConfig.class.getName();
    private static TargetLengthBasedClassNameAbbreviator targetLengthBasedClassNameAbbreviator =
            new TargetLengthBasedClassNameAbbreviator(36);

    @PostConstruct
    private void contextOperatorHook()  {
        Hooks.onEachOperator(MDC_CONTEXT_REACTOR_KEY,
                Operators.lift((scannable, coreSubscriber) -> new MdcContextLifter<>(coreSubscriber)));
    }

    @PreDestroy
    private void cleanupHook()  {
        Hooks.resetOnEachOperator(MDC_CONTEXT_REACTOR_KEY);
    }

    @Before("execution(* com.ulta.dsp..*.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        log.debug("Entered {}.{}()",
                targetLengthBasedClassNameAbbreviator.abbreviate(joinPoint.getTarget().getClass().getName()),
                joinPoint.getSignature().getName());
    }

    @After("execution(* com.ulta.dsp..*.*(..))")
    public void afterMethod(JoinPoint joinPoint) {
        log.debug("Exited {}.{}()",
                targetLengthBasedClassNameAbbreviator.abbreviate(joinPoint.getTarget().getClass().getName()),
                joinPoint.getSignature().getName());
    }
}