package com.phonepe.assignment.service;

import com.phonepe.assignment.exception.MessageProcessorException;
import com.phonepe.assignment.model.Message;
import com.phonepe.assignment.subscriber.Subscriber;

public interface MessageProcessor {
  void process(Message message, Subscriber consumer) throws MessageProcessorException;
}
