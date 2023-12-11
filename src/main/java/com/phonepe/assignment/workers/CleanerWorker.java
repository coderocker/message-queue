package com.phonepe.assignment.workers;

import com.phonepe.assignment.queue.CustomQueue;
import com.phonepe.assignment.utils.Logger;
import com.phonepe.assignment.utils.SimpleLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CleanerWorker implements Runnable {

  private final static Logger logger = SimpleLogger.getInstance(CleanerWorker.class);
  private final Object lock = new Object();
  private final CustomQueue customQueue;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public CleanerWorker(CustomQueue customQueue) {
    this.customQueue = customQueue;
    this.startCleanerTask();
  }

  public void startCleanerTask() {
    scheduler.scheduleAtFixedRate(this, 0, 1, TimeUnit.MINUTES);
  }

  @Override
  public void run() {
    synchronized (lock) {
      // Check and remove expired messages
      logger.info("Processed total %s message from queue %s", customQueue.getProcessedMessageCount(), customQueue.getClass().getSimpleName());
      logger.info("Cleaner worker is removing messages from %s", customQueue.getClass().getSimpleName());
      customQueue.removeExpiredMessages();
    }
  }

  public void notifyNewMessage() {
    synchronized (lock) {
      lock.notify();
      logger.info("Cleaner worker notified by %s", customQueue.getClass().getSimpleName());
    }
  }

  public void shutdown() {
    scheduler.shutdown();
  }
}

