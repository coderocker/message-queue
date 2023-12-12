package com.lld.messagequeue.publisher;

import com.lld.messagequeue.model.Message;
import com.lld.messagequeue.model.payload.Payload;
import com.lld.messagequeue.subscriber.Subscriber;

public interface Publisher<T> {
  String getId();
  String getName();
  void publish(Payload<T> payload);
  void acknowledge(Subscriber subscriber, Message message);
  void negativeAck(Subscriber subscriber, Message message);
  void republish(Message message);
}
