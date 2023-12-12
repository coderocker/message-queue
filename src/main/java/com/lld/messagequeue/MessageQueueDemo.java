package com.lld.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.lld.messagequeue.model.payload.JsonPayload;
import com.lld.messagequeue.publisher.MessagePublisher;
import com.lld.messagequeue.publisher.Publisher;
import com.lld.messagequeue.queue.MessageQueue;
import com.lld.messagequeue.subscriber.MessageSubscriber;
import com.lld.messagequeue.subscriber.Subscriber;
import com.lld.messagequeue.utils.ContinuesJsonPublisher;
import com.lld.messagequeue.utils.Logger;
import com.lld.messagequeue.utils.SimpleLogger;

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

    ContinuesJsonPublisher continuesJsonPublisherThread = new ContinuesJsonPublisher(2);
    for (int i = 0; i < 200; i++) {
      logger.info("Publishing Message");
      new Thread(continuesJsonPublisherThread).start();
    }
  }
}
