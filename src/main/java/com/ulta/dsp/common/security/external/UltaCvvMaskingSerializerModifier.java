/*
 * Copyright (C) 2026 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.security.external;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A Jackson {@link BeanSerializerModifier} that dynamically finds properties named
 * 'cardVerificationNumber' and assigns a custom serializer to mask their values.
 *
 * <p>This class is the core of the centralized masking solution. It hooks into the startup phase of
 * Jackson's serialization process for any given class. It inspects all properties of a bean and, if
 * it finds one that matches the target field name and type, it replaces the default serializer with
 * the {@link UltaSensitiveFieldSerializer}.
 *
 * <p>This process happens only once per class type and the result is cached by Jackson, ensuring
 * that there is no performance penalty on subsequent serializations.
 */
public class UltaCvvMaskingSerializerModifier extends BeanSerializerModifier {

  /** The name of the property to be targeted for masking. */
  private final Set<String> fieldsToMask;

  /**
   * A single, shared instance of the serializer to be used for all matching properties. This avoids
   * creating unnecessary objects.
   */
  private static final UltaSensitiveFieldSerializer SENSITIVE_FIELD_SERIALIZER =
      new UltaSensitiveFieldSerializer();

  /**
   * Constructs a new serializer modifier that masks the default field "securityCode",
   * "cardVerificationNumber", "cvv".
   *
   * <p>This is a convenience constructor that delegates to {@link
   * #UltaCvvMaskingSerializerModifier(Set)} with a default set of field names.
   */
  public UltaCvvMaskingSerializerModifier() {
    this(Set.of("securityCode", "cardVerificationNumber", "cvv"));
  }

  /**
   * Constructs a new serializer modifier with a specified set of fields to mask.
   *
   * @param fieldsToMask A set of property names that should be masked by the serializer.
   */
  public UltaCvvMaskingSerializerModifier(Set<String> fieldsToMask) {
    this.fieldsToMask = fieldsToMask;
  }

  /**
   * Overridden method from {@link BeanSerializerModifier} to inspect and change bean properties.
   *
   * @param config The current serialization configuration.
   * @param beanDesc The description of the bean being serialized.
   * @param beanProperties The list of writable properties for the bean.
   * @return The (potentially modified) list of bean properties.
   */
  @Override
  public List<BeanPropertyWriter> changeProperties(
      SerializationConfig config,
      BeanDescription beanDesc,
      List<BeanPropertyWriter> beanProperties) {

    // Use a functional stream to find the target property and assign the custom serializer.
    beanProperties.stream()
        .filter(
            writer ->
                Objects.nonNull(writer)
                    && fieldsToMask.contains(writer.getName())
                    && writer.getType().getRawClass().equals(String.class))
        .forEach(writer -> writer.assignSerializer(SENSITIVE_FIELD_SERIALIZER));

    return beanProperties;
  }
}