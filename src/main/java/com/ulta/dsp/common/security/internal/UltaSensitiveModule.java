/*
 * Copyright (C) 2026 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.security.internal;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ulta.dsp.common.domain.data.UltaField;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Jackson {@link SimpleModule} that dynamically applies the {@link UltaSensitiveDataSerializer}
 * to properties annotated with {@link UltaSensitive}.
 *
 * <p>This approach is highly performant because it uses a {@link BeanSerializerModifier} to inspect
 * bean properties only once during the initial serializer construction. It modifies only the
 * specific properties that are annotated, leaving all other properties to be handled by Jackson's
 * default, highly optimized serializers.
 */
public class UltaSensitiveModule extends SimpleModule {

  /** The unique name of this Jackson module. */
  private static final String MODULE_NAME = "UltaSensitiveModule";

  /**
   * A thread-safe cache for {@link UltaSensitiveDataSerializer} instances.
   *
   * <p>This cache ensures that only one serializer instance is created per {@link UltaMaskType}.
   * Serializers are created on-demand and then reused, which is an efficient and scalable approach.
   */
  private final Map<UltaMaskType, JsonSerializer<Object>> serializerCache =
      new ConcurrentHashMap<>();

  /**
   * Constructs the module and registers a {@link BeanSerializerModifier} that applies the {@link
   * UltaSensitiveDataSerializer} to annotated fields.
   */
  public UltaSensitiveModule() {
    super(MODULE_NAME);
    /*
     * Use a BeanSerializerModifier to dynamically apply the sensitive data serializer. This is more
     * efficient than registering a global serializer for a common type like String, as it only
     * affects properties that are explicitly annotated.
     */
    setSerializerModifier(
        new BeanSerializerModifier() {
          @Override
          public List<BeanPropertyWriter> changeProperties(
              SerializationConfig config,
              BeanDescription beanDesc,
              List<BeanPropertyWriter> beanProperties) {
            /* This loop iterates through all properties of a bean during its initial setup.*/
            beanProperties.forEach(
                writer -> {
                  /*
                   * If the @UltaSensitive annotation is present, assign the serializer. This module
                   * intentionally does not check the property's type, relying on the
                   * UltaSensitiveDataSerializer to handle any object by converting it to a String.
                   */
                  final UltaSensitive sensitive = writer.getAnnotation(UltaSensitive.class);
                  final Class<?> propertyType = writer.getType().getRawClass();
                  if (Objects.nonNull(sensitive)
                      && (propertyType == String.class
                          || UltaField.class.isAssignableFrom(propertyType))) {
                    writer.assignSerializer(getOrCreateSerializer(sensitive.type()));
                  }
                });
            return beanProperties;
          }
        });
  }

  /**
   * Lazily retrieves a serializer from the cache or creates and caches a new one if it's not
   * present.
   *
   * <p>This method uses {@link ConcurrentHashMap#computeIfAbsent} for a thread-safe, non-blocking
   * way to ensure that the {@link UltaSensitiveDataSerializer} is instantiated only once for each
   * {@link UltaMaskType}.
   *
   * @param maskType The mask type for which to get a serializer.
   * @return A cached {@link JsonSerializer} instance.
   */
  private JsonSerializer<Object> getOrCreateSerializer(UltaMaskType maskType) {
    return serializerCache.computeIfAbsent(maskType, UltaSensitiveDataSerializer::new);
  }
}