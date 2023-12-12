package com.lld.messagequeue.queue;

import com.lld.messagequeue.model.Message;
import com.lld.messagequeue.utils.SimpleLogger;
import com.lld.messagequeue.workers.CleanerWorker;

import java.time.Duration;
import java.util.LinkedList;

public class DeadLetterQueue implements CustomQueue{

  private static DeadLetterQueue instance;
  private final LinkedList<Message> messages = new LinkedList<>();
  private final Duration ttl = Duration.ofMinutes(1);
  private final static SimpleLogger logger = SimpleLogger.getInstance(DeadLetterQueue.class);
  private final CleanerWorker cleanerWorker;


  private DeadLetterQueue(){
    this.cleanerWorker = new CleanerWorker(this);
  }

  public static synchronized DeadLetterQueue getInstance() {
    if (instance == null) {
      instance = new DeadLetterQueue();
    }
    return instance;
  }

  @Override
  public boolean enqueue(Message message) {
    messages.addLast(message);
    cleanerWorker.notifyNewMessage();
    logger.warn("Message with message id %s added to Dead Letter Queue with below content %n%s", message.getId(), message.getPayload().toString());
    return true;
  }

  @Override
  public synchronized Message dequeue() {
    Message message = isEmpty() ? null : messages.poll();
    if(message != null) {
      logger.warn("Message with message id %s removed from Dead Letter Queue with below content %n%s", message.getId(), message.getPayload().toString());
    }
    return message;
  }

  @Override
  public synchronized Message peek() {
    return isEmpty() ? null : messages.peek();
  }

  @Override
  public synchronized int size() {
    return messages.size();
  }

  @Override
  public synchronized boolean isEmpty() {
    return messages.isEmpty();
  }

  @Override
  public synchronized void removeExpiredMessages() {
    Message message = this.peek();
    if (message != null && message.isExpired(ttl)) {
      logger.info("Message with id %s is expired and remove from Dead Letter Queue%n%s", message.getId(), message);
      this.dequeue();
    }
  }

  @Override
  public int getProcessedMessageCount() {
    return 0;
  }
}
