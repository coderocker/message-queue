package com.phonepe.assignment.publisher;

import com.phonepe.assignment.model.Message;
import com.phonepe.assignment.model.payload.Payload;
import com.phonepe.assignment.subscriber.Subscriber;

public interface Publisher<T> {
  String getId();
  String getName();
  void publish(Payload<T> payload);
  void acknowledge(Subscriber subscriber, Message message);
  void negativeAck(Subscriber subscriber, Message message);
  void republish(Message message);
}
