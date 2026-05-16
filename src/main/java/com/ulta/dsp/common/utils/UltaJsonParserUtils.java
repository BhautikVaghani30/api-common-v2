/*
 * Copyright (C) 2024 ULTA
 * http://www.ulta.com
 * All rights reserved
 */
package com.ulta.dsp.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class UltaJsonParserUtils {
  private static final ObjectMapper MAPPER;

  static {
    MAPPER = new ObjectMapper();
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    MAPPER.registerModule(new JavaTimeModule());
    MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  private UltaJsonParserUtils() {}

  /**
   * Takes an object as parameter and converts in json string format like
   *
   * @return string
   */
  public static String getJsonFromObject(final Object object){

    try {
      return MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  /**
   * Takes an object as parameter and converts in json string format
   *
   * @return string
   */
  public static String getJsonFromObjectWithoutSensitiveData(final Object object, final List<String> list) {
    if (ObjectUtils.isEmpty(object)) {
      return null;
    }
    try {
      return removeSensitiveData(MAPPER.writeValueAsString(object), list);
    } catch (Exception e) {
      if(e instanceof Exception || e instanceof IOException || e instanceof JsonProcessingException) {
        log.error(
            "Entity Name - UltaJsonParserUtils, Process Name - getJsonFromObjectWithoutSensitiveData object : {}, failed with Exception : {}", object, e.getMessage());
      }
      return null;
    }
  }

  /**
   * Takes a json string as parameter and remove the defined sensitive fields
   *
   * @param jsonStr the json string
   * @param sensitiveFields list of sensitive fields to be removed from json
   * @return string
   */
  public static String removeSensitiveData(String jsonStr, List<String> sensitiveFields)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(jsonStr);
    removeSensitive(jsonNode, sensitiveFields);
    return mapper.writeValueAsString(jsonNode);
  }

  /**
   * Takes a json node as parameter and remove the defined sensitive fields
   *
   * @param jsonNode
   * @param sensitiveFields list of sensitive fields to be removed from json object
   */
  private static void removeSensitive(JsonNode jsonNode, List<String> sensitiveFields) {
    if (jsonNode.isObject()) {
      ObjectNode objectNode = (ObjectNode) jsonNode;
      sensitiveFields.forEach(objectNode::remove);
      Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
      while (iter.hasNext()) {
        removeSensitive(iter.next().getValue(), sensitiveFields);
      }
    } else if (jsonNode.isArray()){
      for(JsonNode node : jsonNode){
        removeSensitive(node, sensitiveFields);
      }
    }
  }
}