package com.phonepe.assignment.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.phonepe.assignment.model.JsonMessage;
import com.phonepe.assignment.model.Message;
import com.phonepe.assignment.model.payload.JsonPayload;
import com.phonepe.assignment.publisher.MessagePublisher;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageQueueTest {

  @Test
  void testEnqueueDequeue() throws JsonProcessingException {
    MessageQueue messageQueue = MessageQueue.getInstance();

    // Enqueue a message
    String jsonString = "{\"key\": \"value\", \"number\": 42}";
    JsonPayload payload = new JsonPayload(jsonString);
    JsonMessage message = new JsonMessage(new MessagePublisher("test 1"), payload);

    messageQueue.enqueue(message);

    // Dequeue the message
    Message dequeuedMessage = messageQueue.dequeue();

    // Ensure the dequeued message is the same as the enqueued message
    assertEquals(message, dequeuedMessage);
  }
}
