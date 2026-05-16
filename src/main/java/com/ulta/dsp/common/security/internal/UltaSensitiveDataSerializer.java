/*
 * Copyright (C) 2026 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.security.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ulta.dsp.common.domain.data.UltaField;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * A custom Jackson serializer to mask sensitive string data.
 *
 * <p>This serializer is intended to be applied exclusively to fields annotated with {@link
 * UltaSensitive}. The {@link UltaSensitiveModule} is responsible for finding such fields and
 * assigning an instance of this serializer to them, configured with the appropriate {@link
 * UltaMaskType}.
 */
public class UltaSensitiveDataSerializer extends JsonSerializer<Object> {
  private static final char MASK_CHARACTER = '*';
  private static final int PARTIAL_VISIBLE_CHARS = 4;
  private static final int PAN_PREFIX_LENGTH = 6;
  private static final int PAN_SUFFIX_LENGTH = 4;
  private static final int MIN_PAN_LENGTH_FOR_MASK = PAN_PREFIX_LENGTH + PAN_SUFFIX_LENGTH + 1;
  private static final int EMAIL_USERNAME_VISIBLE_CHARS = 1;

  private final UltaMaskType maskType;

  /**
   * Constructs a serializer with a specific masking strategy. This constructor is called by the
   * {@link UltaSensitiveModule} when an {@link UltaSensitive} annotation is detected on a field.
   *
   * @param maskType The type of masking to be applied by this serializer instance.
   */
  public UltaSensitiveDataSerializer(UltaMaskType maskType) {
    this.maskType = maskType;
  }

  /**
   * Handles the top-level serialization logic.
   *
   * <p>It writes a JSON {@code null} if the input object is null or if the masking type is not
   * configured. Otherwise, it delegates to {@link #maskSensitiveData(Object, UltaMaskType)} to get
   * the masked value and writes the result to the {@link JsonGenerator}. If the masked value is
   * null or empty, a JSON {@code null} is written.
   *
   * @param object The object to serialize.
   * @param jsonGenerator The JSON generator to write the output to.
   * @param serializerProvider The provider for accessing serializers.
   * @throws IOException If an I/O error occurs during serialization.
   */
  @Override
  public void serialize(
      Object object, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
      throws IOException {
    if (Objects.isNull(object) || Objects.isNull(maskType)) {
      serializerProvider.defaultSerializeNull(jsonGenerator);
    } else {
      String maskedValue = maskSensitiveData(object, maskType);
      if (Objects.isNull(maskedValue)) {
        serializerProvider.defaultSerializeNull(jsonGenerator);
      } else {
        jsonGenerator.writeString(maskedValue);
      }
    }
  }

  /**
   * Extracts the string value from the input object and dispatches to the appropriate masking
   * method.
   *
   * <p>This method first unwraps {@link UltaField} instances or converts any other object to its
   * string representation. If the resulting string is null or empty, it returns null. Otherwise, it
   * uses a {@code switch} expression to call the correct private masking implementation.
   *
   * @param object The original object to be masked.
   * @param maskType The type of masking to apply.
   * @return The masked string, or {@code null} if the input is null or empty.
   * @throws IllegalArgumentException If an unsupported {@link UltaMaskType} is encountered, though
   *     this should ideally not happen with the current enum.
   */
  private String maskSensitiveData(Object object, UltaMaskType maskType) {

    /*
     * Use Optional to gracefully handle null inputs and extract the value to be masked.
     * This chain ensures that if the object, its inner value, or its string form is null/empty,
     * we exit early without errors.
     */
    final String value =
        Optional.ofNullable(object)
            .map(obj -> (obj instanceof UltaField) ? ((UltaField) obj).getValue() : obj)
            .map(
                String
                    ::valueOf) /* Converts any value—primitive or object—into its String representation */
            .filter(StringUtils::isNotEmpty)
            .orElse(null);

    // If there is no value to mask, return null to represent an empty field in JSON.
    if (value == null) return null;

    // Dispatch to the appropriate masking method based on the mask type.
    return switch (maskType) {
      case FULL, CVV -> maskFull(value);
      case PARTIAL -> maskPartial(value);
      case PAN -> maskPan(value);
      case EMAIL -> maskEmail(value);
      default -> throw new IllegalArgumentException("Unsupported UltaMaskType: " + maskType);
    };
  }

  /**
   * Masks the entire string by replacing every character with a mask character.
   *
   * @param value The string to mask.
   * @return A new string of the same length, composed entirely of mask characters.
   */
  private String maskFull(String value) {
    return StringUtils.repeat(MASK_CHARACTER, value.length());
  }

  /**
   * Masks the beginning of a string, revealing only the last four characters.
   *
   * <p>If the input string has 4 or fewer characters, it will be fully masked.
   *
   * @param value The string to partially mask.
   * @return The partially masked string (e.g., "******7890").
   */
  private String maskPartial(String value) {
    if (value.length() <= PARTIAL_VISIBLE_CHARS) {
      return maskFull(value);
    }
    return StringUtils.repeat(MASK_CHARACTER, value.length() - PARTIAL_VISIBLE_CHARS)
        + value.substring(value.length() - PARTIAL_VISIBLE_CHARS);
  }

  /**
   * Masks a Primary Account Number (PAN), revealing the first six and last four digits.
   *
   * <p>If the input string is too short to be meaningfully masked (less than 11 characters), it
   * will be fully masked.
   *
   * @param value The PAN string to mask.
   * @return The masked PAN (e.g., "123456******3456").
   */
  private String maskPan(String value) {
    if (value.length() < MIN_PAN_LENGTH_FOR_MASK) {
      return maskFull(value);
    }

    return value.substring(0, PAN_PREFIX_LENGTH)
        + StringUtils.repeat(MASK_CHARACTER, value.length() - PAN_PREFIX_LENGTH - PAN_SUFFIX_LENGTH)
        + value.substring(value.length() - PAN_SUFFIX_LENGTH);
  }

  /**
   * Masks an email address, revealing the first character of the username and the full domain.
   *
   * <p>Edge cases:
   *
   * <ul>
   *   <li>If the email format is invalid (e.g., no '@' symbol), it is fully masked.
   *   <li>If the username part is only one character long, it is fully masked.
   * </ul>
   *
   * @param value The email string to mask.
   * @return The masked email (e.g., "j*******@example.com").
   */
  private String maskEmail(String value) {
    int atIndex = value.indexOf('@');

    // No '@' or '@' is the first character (e.g., "@domain.com" or "user.com")
    if (atIndex <= 0) {
      return maskFull(value);
    }

    String username = value.substring(0, atIndex);
    String domain = value.substring(atIndex);

    if (username.length() <= EMAIL_USERNAME_VISIBLE_CHARS) {
      // If username is only one char, mask it completely.
      return MASK_CHARACTER + domain;
    }

    return username.charAt(0)
        + StringUtils.repeat(MASK_CHARACTER, username.length() - EMAIL_USERNAME_VISIBLE_CHARS)
        + domain;
  }
}