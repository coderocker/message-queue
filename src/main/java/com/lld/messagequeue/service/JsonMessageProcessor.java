package com.lld.messagequeue.service;

import com.lld.messagequeue.model.Message;
import com.lld.messagequeue.subscriber.Subscriber;
import com.lld.messagequeue.utils.Logger;
import com.lld.messagequeue.utils.SimpleLogger;

public class JsonMessageProcessor implements MessageProcessor{
  private final static Logger logger = SimpleLogger.getInstance(JsonMessageProcessor.class);

  @Override
  public void process(Message message, Subscriber subscriber)  {
    logger.info("Subscriber %s successfully processed message %s", subscriber.getName(), message.getPayload().toString());
  }
}
