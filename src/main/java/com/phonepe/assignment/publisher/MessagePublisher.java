package com.phonepe.assignment.publisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.phonepe.assignment.model.JsonMessage;
import com.phonepe.assignment.model.Message;
import com.phonepe.assignment.model.payload.Payload;
import com.phonepe.assignment.queue.MessageQueue;
import com.phonepe.assignment.subscriber.Subscriber;
import com.phonepe.assignment.utils.Logger;
import com.phonepe.assignment.utils.SimpleLogger;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MessagePublisher implements Publisher<JsonNode> {

  private final String id;
  private final String name;
  private final MessageQueue messageQueue;
  private final static Logger logger = SimpleLogger.getInstance(MessagePublisher.class);

  public MessagePublisher(String name) {
    this.name = name;
    this.id = UUID.randomUUID().toString();
    this.messageQueue = MessageQueue.getInstance();
  }

  @Override
  public void publish(Payload<JsonNode> payload) {
    Message message = new JsonMessage(this, payload);
    messageQueue.publish(message);
  }

  @Override
  public void acknowledge(Subscriber subscriber, Message message) {
    logger.info("Message with messageId %s successfully processed by Subscriber %s message payload was %n%s", message.getId(), subscriber.getName(), message.getPayload().toString());
  }

  @Override
  public void negativeAck(Subscriber subscriber, Message message) {
    logger.info("Message with messageId %s was not processed by Subscriber %s message payload was %n%s", message.getId(), subscriber.getName(), message.getPayload().toString());
  }

  @Override
  public void republish(Message message) {
    if(message.getPublishCount() < 3) {
      messageQueue.publish(message);
      logger.info("Publisher %s is trying to republish the message %n%s", this.getName(), message.getPayload().toString());
    }
  }
}
