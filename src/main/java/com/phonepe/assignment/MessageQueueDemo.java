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

import java.util.List;

public class MessageQueueDemo {
  public static void main(String[] args) throws JsonProcessingException {
    MessageQueue messageQueue = MessageQueue.start();

    Publisher<JsonNode> publisher = new MessagePublisher("First Publisher");

    String jsonString = "{\"name\":\"John\",\"age\":25,\"city\":\"New York\"}";
    publisher.publish(new JsonPayload(jsonString));
    publisher.publish(new JsonPayload(jsonString));
    publisher.publish(new JsonPayload(jsonString));
    publisher.publish(new JsonPayload(jsonString));

    Subscriber subscriber = new MessageSubscriber("First");
    Subscriber subscriber1 = new MessageSubscriber("Second", List.of("(?i)(?s).*\"city\".*"));

    subscriber1.addDependent(subscriber);
    messageQueue.subscribe(subscriber1);



//    messageQueue.subscribe(subscriber1);

    ContinuesJsonPublisher continuesJsonPublisherThread = new ContinuesJsonPublisher(3);
    new Thread(continuesJsonPublisherThread).start();
  }
}
