package com.lld.messagequeue.model.payload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonPayloadTest {

  @Test
  void testConstructorWithValidJsonString() {
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    assertDoesNotThrow(() -> new JsonPayload(jsonString));
  }

  @Test
  void testConstructorWithInvalidJsonString() {
    String invalidJsonString = "invalid_json";
    assertThrows(Exception.class, () -> new JsonPayload(invalidJsonString));
  }

  @Test
  void testGetPayload() throws Exception {
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload jsonPayload = new JsonPayload(jsonString);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode expectedPayload = objectMapper.readTree(jsonString);

    assertEquals(expectedPayload, jsonPayload.getPayload());
  }
}
