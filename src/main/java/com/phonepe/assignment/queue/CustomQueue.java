package com.phonepe.assignment.queue;

import com.phonepe.assignment.model.Message;


public interface CustomQueue {
  boolean enqueue(Message message);
  Message dequeue();
  Message peek(); // New method for removing and returning the head of the queue
  boolean isEmpty();
  int size();
  void removeExpiredMessages();
}
