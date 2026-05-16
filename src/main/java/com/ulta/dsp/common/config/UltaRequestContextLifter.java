/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.config;

import com.ulta.dsp.common.context.UltaContext;
import com.ulta.dsp.common.model.UltaRequestContext;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

class UltaRequestContextLifter<T> implements CoreSubscriber<T> {

  CoreSubscriber<T> coreSubscriber;

  public UltaRequestContextLifter(CoreSubscriber<T> coreSubscriber) {
    this.coreSubscriber = coreSubscriber;
  }

  @Override
  public void onSubscribe(Subscription subscription) {
    coreSubscriber.onSubscribe(subscription);
  }

  @Override
  public void onNext(T obj) {
    copyToUltaContext(coreSubscriber.currentContext());
    coreSubscriber.onNext(obj);
  }

  @Override
  public void onError(Throwable t) {
    try{
      coreSubscriber.onError(t);
    } finally {
      UltaContext.clear();
    }
    
  }

  @Override
  public void onComplete() {
    try {
      coreSubscriber.onComplete();
    } finally {
      UltaContext.clear();
    }
  }

  @Override
  public Context currentContext() {
    return coreSubscriber.currentContext();
  }

  /**
   * Extension function for the Reactor [Context]. Copies the current context to the UltaContext, if
   * context is empty clears the UltaContext. State of the UltaContext after calling this method
   * should be same as Reactor [Context] state. One thread-local access only.
   */
  private void copyToUltaContext(Context context) {
    if (!context.isEmpty()) {

      UltaRequestContext requestContext =
          (UltaRequestContext)
              context
                  .getOrEmpty(
                      com.ulta.dsp.common.constants.UltaCommonConstants.REQUEST_CONTEXT_NAME)
                  .orElse(null);

      UltaContext.setContext(requestContext);
    } else {
      UltaContext.clear();
    }
  }
}