package com.lld.messagequeue.model.payload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonPayload implements Payload<JsonNode> {

  private final JsonNode payload;

  public JsonPayload(String jsonString) throws JsonProcessingException {
    this.payload = transformJsonStringToJson(jsonString);
  }

  private JsonNode transformJsonStringToJson(String jsonString) throws JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper();
      // Convert JSON string to JsonNode
    return objectMapper.readTree(jsonString);
  }

  @Override
  public JsonNode getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    return payload.toPrettyString();
  }
}
