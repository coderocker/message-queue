package com.lld.messagequeue.service;

import com.lld.messagequeue.exception.MessageProcessorException;
import com.lld.messagequeue.model.Message;
import com.lld.messagequeue.subscriber.Subscriber;

public interface MessageProcessor {
  void process(Message message, Subscriber consumer) throws MessageProcessorException;
}
