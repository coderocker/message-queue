package com.phonepe.assignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.phonepe.assignment.model.payload.JsonPayload;
import com.phonepe.assignment.publisher.MessagePublisher;
import com.phonepe.assignment.publisher.Publisher;
import com.phonepe.assignment.queue.MessageQueue;
import com.phonepe.assignment.subscriber.MessageSubscriber;
import com.phonepe.assignment.subscriber.Subscriber;
import com.phonepe.assignment.utils.ContinuesJsonPublisher;
import com.phonepe.assignment.utils.Logger;
import com.phonepe.assignment.utils.SimpleLogger;

import java.util.List;

public class MessageQueueDemo {
  private static final Logger logger = SimpleLogger.getInstance(MessageQueue.class);
  public static void main(String[] args) throws JsonProcessingException {
    MessageQueue messageQueue = MessageQueue.start();

    Publisher<JsonNode> publisher = new MessagePublisher("First Publisher");

    String jsonString = "{\"name\":\"John\",\"age\":25,\"city\":\"New York\"}";
    publisher.publish(new JsonPayload(jsonString));
    publisher.publish(new JsonPayload(jsonString));
    publisher.publish(new JsonPayload(jsonString));
    publisher.publish(new JsonPayload(jsonString));

    for (int i = 0; i < 5; i++) {
      Subscriber subscriber = new MessageSubscriber(String.format("Consumer-%d", i+1));
      messageQueue.subscribe(subscriber);
    }
//    Subscriber subscriber1 = new MessageSubscriber("Second", List.of("(?i)(?s).*\"city\".*"));

//    subscriber1.addDependent(subscriber);
//    messageQueue.subscribe(subscriber1);
    publisher.publish(new JsonPayload("{\"name\":\"John\",\"age\":25,\"city\":\"Pune\"}"));


    for (int i = 0; i < 200; i++) {
      logger.info("Creating Publisher");
      ContinuesJsonPublisher continuesJsonPublisherThread = new ContinuesJsonPublisher(1);
      new Thread(continuesJsonPublisherThread).start();
    }
  }
}
