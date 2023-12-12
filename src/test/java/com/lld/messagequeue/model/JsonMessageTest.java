package com.lld.messagequeue.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lld.messagequeue.model.payload.JsonPayload;
import com.lld.messagequeue.publisher.MessagePublisher;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class JsonMessageTest {

  @Test
  void testJsonMessageCreation() {
    MessagePublisher publisher = new MessagePublisher("TestPublisher");
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload payload = assertDoesNotThrow(() -> new JsonPayload(jsonString));

    JsonMessage jsonMessage = new JsonMessage(publisher, payload);

    assertNotNull(jsonMessage.getId());
    assertEquals(publisher, jsonMessage.getPublisher());
    assertEquals(payload, jsonMessage.getPayload());
    assertNotNull(jsonMessage.getCreationTime());
    assertEquals(0, jsonMessage.getRetryCount());
    assertEquals(0, jsonMessage.getPublishCount());
  }

  @Test
  void testIncrementRetryCount() {
    MessagePublisher publisher = new MessagePublisher("TestPublisher");
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload payload = assertDoesNotThrow(() -> new JsonPayload(jsonString));

    JsonMessage jsonMessage = new JsonMessage(publisher, payload);
    jsonMessage.incrementRetryCount();

    assertEquals(1, jsonMessage.getRetryCount());
  }

  @Test
  void testResetRetryCount() {
    MessagePublisher publisher = new MessagePublisher("TestPublisher");
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload payload = assertDoesNotThrow(() -> new JsonPayload(jsonString));

    JsonMessage jsonMessage = new JsonMessage(publisher, payload);
    jsonMessage.incrementRetryCount();
    jsonMessage.resetRetryCount();

    assertEquals(0, jsonMessage.getRetryCount());
  }

  @Test
  void testIncrementPublishCount() {
    MessagePublisher publisher = new MessagePublisher("TestPublisher");
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload payload = assertDoesNotThrow(() -> new JsonPayload(jsonString));

    JsonMessage jsonMessage = new JsonMessage(publisher, payload);
    jsonMessage.incrementPublishCount();

    assertEquals(1, jsonMessage.getPublishCount());
  }

  @Test
  void testGetExpiry() {
    MessagePublisher publisher = new MessagePublisher("TestPublisher");
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload payload = assertDoesNotThrow(() -> new JsonPayload(jsonString));

    JsonMessage jsonMessage = new JsonMessage(publisher, payload);

    Duration ttl = Duration.ofSeconds(30);
    assertNotNull(jsonMessage.getExpiry(ttl));
  }

  @Test
  void testIsExpired() throws InterruptedException {
    MessagePublisher publisher = new MessagePublisher("TestPublisher");
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload payload = assertDoesNotThrow(() -> new JsonPayload(jsonString));

    JsonMessage jsonMessage = new JsonMessage(publisher, payload);

    // Creating a message with a TTL of 1 second
    Duration ttl = Duration.ofSeconds(1);
    Thread.sleep(1000); // Introduce a delay of 1 second
    assertTrue(jsonMessage.isExpired(ttl));

    // Creating a message with a TTL of 1 day
    Duration longTtl = Duration.ofDays(1);
    assertFalse(jsonMessage.isExpired(longTtl));
  }

  @Test
  void testToString() {
    MessagePublisher publisher = new MessagePublisher("TestPublisher");
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload payload = assertDoesNotThrow(() -> new JsonPayload(jsonString));

    JsonMessage jsonMessage = new JsonMessage(publisher, payload);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    JsonNode expectedJsonNode = objectMapper.valueToTree(jsonMessage);

    assertEquals(expectedJsonNode.toPrettyString(), jsonMessage.toString());
  }
}
