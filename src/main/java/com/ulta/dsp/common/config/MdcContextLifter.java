/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.config;

import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

import java.util.Map;
import java.util.stream.Collectors;

class MdcContextLifter<T> implements CoreSubscriber<T> {

    CoreSubscriber<T> coreSubscriber;

    public MdcContextLifter(CoreSubscriber<T> coreSubscriber) {
        this.coreSubscriber = coreSubscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        coreSubscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T obj) {
        copyToMdc(coreSubscriber.currentContext());
        coreSubscriber.onNext(obj);
    }

    @Override
    public void onError(Throwable t) {
        try {
            coreSubscriber.onError(t);
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void onComplete() {
        try {
            coreSubscriber.onComplete();
        } finally {
            MDC.clear();
        }
    }

    @Override
    public Context currentContext() {
        return coreSubscriber.currentContext();
    }

    /**
     * Extension function for the Reactor [Context]. Copies the current context to the MDC, if context is empty clears the MDC.
     * State of the MDC after calling this method should be same as Reactor [Context] state.
     * One thread-local access only.
     */
    private void copyToMdc(Context context) {
        if (!context.isEmpty()) {
            Map<String, String> map = context.stream()
                    .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));

            MDC.setContextMap(map);
        } else {
            MDC.clear();
        }
    }

}