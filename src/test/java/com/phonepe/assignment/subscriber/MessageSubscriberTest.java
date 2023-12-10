package com.phonepe.assignment.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.phonepe.assignment.model.JsonMessage;
import com.phonepe.assignment.model.Message;
import com.phonepe.assignment.model.payload.JsonPayload;
import com.phonepe.assignment.publisher.MessagePublisher;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageSubscriberTest {

  @Test
  void testOnMessageReceivedMatchingPattern() throws JsonProcessingException {
    // Mock data
    String subscriberName = "TestSubscriber";
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload payload = new JsonPayload(jsonString);
    JsonMessage message = new JsonMessage(new MessagePublisher(subscriberName), payload);

    // Create a subscriber with a matching process pattern
    MessageSubscriber subscriber = new MessageSubscriber(subscriberName);

    // Simulate receiving a message
    subscriber.onMessageReceived(message);

    assertEquals(1, subscriber.getProcessedMessageIds().size());
    assertTrue(subscriber.getProcessedMessageIds().contains(message.getId()));
  }

  @Test
  void testOnMessageReceivedNonMatchingPattern() throws JsonProcessingException {
    String subscriberName = "TestSubscriber";
    String processPattern = "nonMatchingPattern";
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload payload = new JsonPayload(jsonString);
    JsonMessage message = new JsonMessage(new MessagePublisher(subscriberName), payload);

    MessageSubscriber subscriber = new MessageSubscriber(subscriberName, List.of(processPattern));

    subscriber.onMessageReceived(message);

    assertTrue((subscriber.getProcessedMessageIds() == null || subscriber.getProcessedMessageIds().isEmpty()));
  }
}
