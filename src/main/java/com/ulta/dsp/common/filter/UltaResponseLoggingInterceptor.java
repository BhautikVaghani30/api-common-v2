/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

@Slf4j
public class UltaResponseLoggingInterceptor extends ServerHttpResponseDecorator {

  private long startTime;

  public UltaResponseLoggingInterceptor(ServerHttpResponse delegate, long startTime) {
    super(delegate);
    this.startTime = startTime;
  }

  @Override
  public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
    Flux<DataBuffer> buffer = Flux.from(body);
    return super.writeWith(
        buffer.doOnNext(
            dataBuffer -> {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              try {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                String bodyRes = baos.toString("UTF-8");
                log.info(
                    "Response({} ms): status={}, payload={}",
                    System.currentTimeMillis() - startTime,
                    getStatusCode().value(),
                    bodyRes);
              } catch (IOException e) {
                log.error("Error logging response.", e);
              } finally {
                try {
                  baos.close();
                } catch (IOException e) {
                  log.error("Error logging response.", e);
                }
              }
            }));
  }
}