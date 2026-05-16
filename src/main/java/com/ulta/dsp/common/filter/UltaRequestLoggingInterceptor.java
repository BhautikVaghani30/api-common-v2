/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

@Slf4j
public class UltaRequestLoggingInterceptor extends ServerHttpRequestDecorator {

  public UltaRequestLoggingInterceptor(ServerHttpRequest delegate) {
    super(delegate);
  }

  @Override
  public Flux<DataBuffer> getBody() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    return super.getBody()
        .doOnNext(
            dataBuffer -> {
              try {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                String body = baos.toString("UTF-8");
                log.info(
                    "Request: method={}, uri={}, headers={}, payload={}",
                    getDelegate().getMethod(),
                    getDelegate().getPath(),
                    getDelegate().getHeaders(),
                    body);
              } catch (IOException e) {
                log.error("Error logging request.", e);
              } finally {
                try {
                  baos.close();
                } catch (IOException e) {
                  log.error("Error logging request.", e);
                }
              }
            });
  }
}