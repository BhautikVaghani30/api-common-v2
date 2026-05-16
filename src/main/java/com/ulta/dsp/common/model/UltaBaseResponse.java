/*
 * Copyright (C) 2023 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class contains information about the requests traversal within the Digital Store Platform
 *
 * <p>
 */
public abstract class UltaBaseResponse {
  /** {@link UltaMeta} */
  private final UltaMeta meta;

  /** List of {@link UltaMessage} */
  private final List<UltaMessage> messages;

  /**
   * Constructor for a new BaseResponse.
   *
   * @param builder the builder
   */
  protected UltaBaseResponse(final Builder<?> builder) {
    meta = builder.meta;
    messages = builder.messages;
  }

  /**
   * Gets the meta information of the response
   *
   * @return the meta information
   */
  public UltaMeta getMeta() {
    return meta;
  }

  /**
   * Gets the messages for the response
   *
   * @return the messages information
   */
  public List<UltaMessage> getMessages() {
    return messages;
  }

  /**
   * Method that will determine if the object is equal to another object
   *
   * @param o: Object to compare against
   * @return true object is equal; false if null or not equal
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UltaBaseResponse that = (UltaBaseResponse) o;
    return getMeta().equals(that.getMeta()) && getMessages().equals(that.getMessages());
  }

  /**
   * Hashcode of the object
   *
   * @return hash of UltaMeta, code, and Messages
   */
  @Override
  public int hashCode() {
    return Objects.hash(getMeta(), getMessages());
  }

  /**
   * Print the object to string
   *
   * @return Stringified BaseResponse Object
   */
  @Override
  public String toString() {
    return "BaseResponse{" + "meta=" + meta + ", messages=" + messages + '}';
  }

  /**
   * Abstract builder class that will set the attributes and build the final immutable BaseResponse
   * object
   *
   * @param <T>
   */
  public abstract static class Builder<T extends Builder<T>> {
    UltaMeta meta;
    List<UltaMessage> messages;

    public T withMeta(final UltaMeta meta) {
      this.meta = meta;
      return self();
    }

    /**
     * This method allows a list of messages to be added to the BaseResponse
     *
     * @param messages list of {@link UltaMessage}
     * @return BaseResponse builder object
     */
    public T withMessages(final List<UltaMessage> messages) {
      if (null != this.messages) {
        this.messages.addAll(messages);
      } else {
        this.messages = new ArrayList<>();
        this.messages.addAll(messages);
      }

      return self();
    }

    /**
     * This method allows a message to be added to the BaseResponse
     *
     * @param message to add
     * @return BaseResponse builder object
     */
    public T addMessage(final UltaMessage message) {
      if (null == this.messages) {
        this.messages = new ArrayList<>();
        this.messages.add(message);
      } else {
        this.messages.add(message);
      }

      return self();
    }

    /**
     * Build the base response with the given attributes.
     *
     * @return BaseResponse object
     */
    public abstract UltaBaseResponse build();

    /**
     * Method that will return itself for additional fields to be set
     *
     * @return the object
     */
    protected abstract T self();
  }
}