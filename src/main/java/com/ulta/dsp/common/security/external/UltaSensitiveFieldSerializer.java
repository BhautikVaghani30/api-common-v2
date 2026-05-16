/*
 * Copyright (C) 2026 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.security.external;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * A custom Jackson {@link JsonSerializer} to mask the value of a sensitive field like a CVV.
 *
 * <p>This serializer replaces the original field value with a mask of equal length (e.g., "123" ->
 * "***") during JSON serialization. It is designed to be both performant and safe:
 *
 * <ul>
 *   <li><b>Flexibility:</b> It can handle any object by converting it to its string representation
 *       for masking.
 *   <li><b>Safety:</b> It includes robust error handling to prevent serialization failures from
 *       breaking the entire process (e.g., a user's API request). If an error occurs, it logs the
 *       issue and writes a {@code null} value as a safe fallback.
 * </ul>
 */
@Slf4j
public class UltaSensitiveFieldSerializer extends JsonSerializer<Object> {
  /** The character used for masking. */
  private static final char MASK_CHARACTER = '*';

  /**
   * Serializes the given field by masking its value.
   *
   * @param field The object to serialize.
   * @param gen The {@link JsonGenerator} used to write the JSON output.
   * @param serializers The provider for serializers.
   */
  @Override
  public void serialize(Object field, JsonGenerator gen, SerializerProvider serializers) {
    Optional.ofNullable(field)
        .ifPresentOrElse(
            value -> {
              try {
                // Convert the field's value to a string to determine its length.
                String originalValue = String.valueOf(value);
                // Create a mask with the same length as the original value.
                String maskedValue = StringUtils.repeat(MASK_CHARACTER, originalValue.length());
                // Write the masked string to the JSON output.
                gen.writeString(maskedValue);
              } catch (IOException e) {
                // Log the error but do not throw it, to prevent breaking the parent serialization.
                // Instead, write a null value as a safe fallback.
                log.error(
                    "UltaSensitiveFieldSerializer : serialize :: IOException during sensitive field serialization. Falling back to null.",
                    e);
                try {
                  serializers.defaultSerializeNull(gen);
                } catch (IOException ex) {
                  log.error(
                      "UltaSensitiveFieldSerializer : serialize :: Failed to write null fallback during serialization error.",
                      ex);
                }
              }
            },
            () -> {
              try {
                // If the original value is null, write a JSON null.
                serializers.defaultSerializeNull(gen);
              } catch (IOException e) {
                log.error(
                    "UltaSensitiveFieldSerializer : serialize :: IOException while attempting to write a null value.",
                    e);
              }
            });
  }
}