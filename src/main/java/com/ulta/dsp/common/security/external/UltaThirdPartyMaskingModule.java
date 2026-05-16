/*
 * Copyright (C) 2026 ULTA
 * http://www.ulta.com
 * All rights reserved
 */

package com.ulta.dsp.common.security.external;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.Set;

/**
 * A custom Jackson {@link SimpleModule} that registers application-wide serialization
 * modifications.
 *
 * <p>This module serves as the central entry point for hooking custom serialization logic into the
 * Jackson {@code ObjectMapper}. By registering the {@link UltaCvvMaskingSerializerModifier}, it
 * enables a scalable and centralized strategy for masking sensitive data, such as the Card
 * Verification Value (CVV), across the entire application without needing to annotate individual
 * classes.
 */
public class UltaThirdPartyMaskingModule extends SimpleModule {

  /** The unique name of this Jackson module. */
  private static final String MODULE_NAME = "UltaThirdPartyMaskingModule";

  /**
   * Constructs the module and registers the custom {@link UltaCvvMaskingSerializerModifier}.
   *
   * <p>This is the key step that injects our custom property-finding logic into the {@code
   * ObjectMapper}'s serialization process.
   */
  public UltaThirdPartyMaskingModule() {
    super(MODULE_NAME, Version.unknownVersion());
    setSerializerModifier(new UltaCvvMaskingSerializerModifier());
  }

  /**
   * Constructs the module and registers a custom {@link UltaCvvMaskingSerializerModifier} with a
   * specific set of field names to mask.
   *
   * @param fieldsToMask A set of property names to be masked during serialization.
   */
  public UltaThirdPartyMaskingModule(Set<String> fieldsToMask) {
    super(MODULE_NAME, Version.unknownVersion());
    setSerializerModifier(new UltaCvvMaskingSerializerModifier(fieldsToMask));
  }
}