package com.phonepe.assignment.service;

import com.phonepe.assignment.model.Message;
import com.phonepe.assignment.subscriber.Subscriber;
import com.phonepe.assignment.utils.Logger;
import com.phonepe.assignment.utils.SimpleLogger;

public class JsonMessageProcessor implements MessageProcessor{
  private final static Logger logger = SimpleLogger.getInstance(JsonMessageProcessor.class);

  @Override
  public void process(Message message, Subscriber subscriber)  {
    logger.info("Subscriber %s successfully processed message %s", subscriber.getName(), message.getPayload().toString());
  }
}
