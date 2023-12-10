package com.phonepe.assignment.workers;


import com.phonepe.assignment.model.Message;
import com.phonepe.assignment.queue.MessageQueue;
import com.phonepe.assignment.subscriber.Subscriber;
import com.phonepe.assignment.utils.Logger;
import com.phonepe.assignment.utils.SimpleLogger;

public class SubscriberWorker implements Runnable{

  private final static Logger logger = SimpleLogger.getInstance(SubscriberWorker.class);
  private final Subscriber subscriber;
  private final MessageQueue messageQueue = MessageQueue.getInstance();
  public SubscriberWorker(Subscriber subscriber) {
    this.subscriber = subscriber;
  }

  @Override
  public void run() {
    synchronized (subscriber) {
      do {

        logger.info("%s subscriber worker started consuming messages from %s queue", subscriber.getName(), messageQueue.getClass().getSimpleName());

        logger.info("%s subscriber worker current offset is %s and queue size is %s", subscriber.getName(), subscriber.getCurrentOffset(), messageQueue.size());
        while (subscriber.getCurrentOffset() >= messageQueue.size()) {
          try {
            logger.info("%s subscriber worker in waiting state, current offset is %s and queue size is %s", subscriber.getName(), subscriber.getCurrentOffset(), messageQueue.size());
            subscriber.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        Message message = messageQueue.getMessage(subscriber.getCurrentOffset());
        subscriber.onMessageReceived(message);
        messageQueue.checkRetryCountExhausted(subscriber.getCurrentOffset());
      } while (true);
    }
  }

  synchronized public void wakeUp() {
    synchronized (subscriber) {
      subscriber.notify();
      logger.info("%s subscriber worker notified by %s", subscriber.getName(), messageQueue.getClass().getSimpleName());
    }
  }
}
