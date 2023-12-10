package com.phonepe.assignment.queue;

import com.phonepe.assignment.subscriber.Subscriber;
import com.phonepe.assignment.utils.Logger;
import com.phonepe.assignment.workers.CleanerWorker;
import com.phonepe.assignment.workers.SubscriberWorker;
import com.phonepe.assignment.model.Message;
import com.phonepe.assignment.utils.SimpleLogger;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MessageQueue implements CustomQueue {
  private static final ReentrantLock lock = new ReentrantLock();
  private static final Condition notFullSignal = lock.newCondition();
  private static final Condition notEmptySignal = lock.newCondition();

  private static MessageQueue instance;
  private static final int maxRetries = 3;
  private final DeadLetterQueue deadLetterQueue = DeadLetterQueue.getInstance();
  private final static Logger logger = SimpleLogger.getInstance(MessageQueue.class);
  private final LinkedList<Message> messages = new LinkedList<>();
  private final Duration ttl = Duration.ofSeconds(30);
  private final List<Subscriber> subscribers = new ArrayList<>();
  private final Map<String, SubscriberWorker> subscriberWorkers = new HashMap<>();
  private final CleanerWorker cleanerWorker;


  private int maxCapacity = 10;

  private MessageQueue(){
    this.cleanerWorker = new CleanerWorker(this);
  }

  private MessageQueue(int maxCapacity) {
    this.maxCapacity = maxCapacity;
    this.cleanerWorker = new CleanerWorker(this);
  }

  public static MessageQueue start() {
    return getInstance();
  }

  public static MessageQueue start(int maxCapacity) {
    return getInstance(maxCapacity);
  }

  public static synchronized MessageQueue getInstance() {
    if (instance == null) {
      instance = new MessageQueue();
    }
    return instance;
  }

  public static synchronized MessageQueue getInstance(int maxCapacity) {
    if (instance == null) {
      instance = new MessageQueue(maxCapacity);
    }
    return instance;
  }

  public void publish(Message message) {
    this.removeExpiredMessages();
    cleanerWorker.notifyNewMessage();
    boolean enqueued = enqueue(message);
    if(enqueued) {
      message.incrementPublishCount();
      notifyConsumers();
    }
  }

  @SneakyThrows
  @Override
  public boolean enqueue(Message message) {
    lock.lock();
    try {
      while (size() == maxCapacity && !Thread.currentThread().isInterrupted()) {
        try {
          logger.warn("Message Queue is full, bellow message with message id %s is waiting to be enqueued %n%s", message.getId(), message.getPayload().toString());
          notEmptySignal.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
      messages.addLast(message);
      logger.warn("Bellow message with message id %s is enqueued to message queue %n%s", message.getId(), message.getPayload().toString());
      notEmptySignal.signal();

    } finally {
      lock.unlock();
    }
    return true;
  }

  @Override
  public synchronized Message dequeue() {
    lock.lock();
    try {
      while (messages.isEmpty() && !Thread.currentThread().isInterrupted()) {
        try {
          // wait for notEmpty signal
          notEmptySignal.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
      Message message = messages.poll();
      // send notFull signal
      notFullSignal.signal();
      if(message != null) {
        logger.warn("Bellow message with message id %s is dequeued from the message queue %n%s", message.getId(), message.getPayload().toString());
      }
      for(Subscriber subscriber : subscribers) {
        if(subscriber.getCurrentOffset() >= size()) {
          subscriber.setCurrentOffset(size()-1);
        }

      }
      return message;
    } finally {
      lock.unlock();
    }
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

  public List<Subscriber> getSubscribers() {
    return subscribers;
  }

  // Register a subscriber with a callback
  public synchronized void subscribe(Subscriber subscriber) {
    subscribers.add(subscriber);
    notifyConsumers();
  }

  public Message getMessage(int index) {
    return messages.get(index);
  }

  @Override
  public synchronized void removeExpiredMessages() {
    Message message = this.peek();
    if (message != null && message.isExpired(ttl)) {
      logger.info("Message with id %s is expired%n%s", message.getId(), message.getPayload().toString());
      this.dequeue();
    }
  }

  public synchronized void checkRetryCountExhausted(int index) {
    if(index < messages.size() && messages.get(index).getRetryCount() > maxRetries) {
      Message message = messages.remove(index);
      deadLetterQueue.enqueue(message);
    }
  }

  // Notify all registered consumers about the new message
  void notifyConsumers() {
    if(subscribers.isEmpty()) {
      logger.info("No subscribers are added yet");
      return;
    }
    for (Subscriber subscriber : subscribers) {
      startSubscriberWorker(subscriber);
    }
  }

  private void startSubscriberWorker(Subscriber subscriber) {
    if(!subscriberWorkers.containsKey(subscriber.getId())) {
      final SubscriberWorker subscriberWorker = new SubscriberWorker(subscriber);
      subscriberWorkers.put(subscriber.getId(), subscriberWorker);
      new Thread(subscriberWorker).start();
    }

    final SubscriberWorker subscriberWorker = subscriberWorkers.get(subscriber.getId());
    subscriberWorker.wakeUp();
  }
}
