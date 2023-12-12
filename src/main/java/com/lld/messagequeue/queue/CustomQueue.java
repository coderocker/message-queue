package com.lld.messagequeue.queue;

import com.lld.messagequeue.model.Message;


public interface CustomQueue {
  boolean enqueue(Message message);
  Message dequeue();
  Message peek(); // New method for removing and returning the head of the queue
  boolean isEmpty();
  int size();
  void removeExpiredMessages();
  int getProcessedMessageCount();
}
