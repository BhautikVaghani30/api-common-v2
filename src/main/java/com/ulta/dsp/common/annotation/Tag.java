/*
 * Copyright (C) 2025 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A single key-value pair to be used as a tag on a metric.
 * This is intended to be used within other metric annotations.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Tag {
    /**
     * The key for the metric tag.
     */
    String key();

    /**
     * The value for the metric tag.
     */
    String value();
}