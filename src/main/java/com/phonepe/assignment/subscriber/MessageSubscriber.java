package com.phonepe.assignment.subscriber;

import com.phonepe.assignment.exception.MessageProcessorException;
import com.phonepe.assignment.model.Message;
import com.phonepe.assignment.queue.MessageQueue;
import com.phonepe.assignment.service.JsonMessageProcessor;
import com.phonepe.assignment.service.MessageProcessor;
import com.phonepe.assignment.utils.Logger;
import com.phonepe.assignment.utils.SimpleLogger;

import java.util.*;

public class MessageSubscriber implements Subscriber {
  private static final Logger logger = SimpleLogger.getInstance(MessageSubscriber.class);
  private static final MessageQueue messageQueue = MessageQueue.getInstance();
  private final String name;
  private final String id;
  private List<String> processPatterns;
  private int currentOffset;
  private final MessageProcessor messageProcessor = new JsonMessageProcessor();
  private final List<Subscriber> dependents = new ArrayList<>();
  private final Set<String> processedMessageIds = new HashSet<>();


  public MessageSubscriber(String name) {
    this.name = name;
    this.id = UUID.randomUUID().toString();
    this.currentOffset = 0;
  }

  public MessageSubscriber(String name, List<String> processPatterns) {
    this.name = name;
    this.id = UUID.randomUUID().toString();
    this.processPatterns = processPatterns;
    this.currentOffset = 0;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getCurrentOffset() {
    return currentOffset;
  }

  @Override
  public void decrementCurrentOffset() {
    if(currentOffset != 0) {
      currentOffset--;
    }
  }

  @Override
  public void incrementCurrentOffset() {
    currentOffset++;
  }

  @Override
  public void setCurrentOffset(int offset) {
    currentOffset = offset;
  }

  @Override
  public void addDependents(List<Subscriber> subscribers) {
    for(Subscriber subscriber : subscribers) {
      addDependent(subscriber);
    }
  }

  @Override
  public void addDependent(Subscriber subscriber) {
    if(!subscriber.equals(this) && !subscriber.getDependents().contains(this))  {
      logger.info("Added %s subscriber as a dependent to %s subscriber", subscriber.getName(), getName());
      dependents.add(subscriber);
      messageQueue.subscribe(subscriber);
    } else {
      logger.warn("Unable to add self as a dependent or cyclic dependent %s subscriber for Subscriber %s", subscriber.getName(), this.getName());
    }
  }

  @Override
  public Set<String> getProcessedMessageIds() {
    return processedMessageIds;
  }

  @Override
  public boolean isMessageProcessed(String id) {
    return processedMessageIds.contains(id);
  }

  @Override
  public void removeProcessedMessage(String id) {
    processedMessageIds.remove(id);
  }

  @Override
  public List<Subscriber> getDependents() {
    return dependents;
  }

  @Override
  public boolean canProcess(String messageId) {
    if(dependents.isEmpty()) {
      logger.info("%s subscriber checking canProcess and found no dependents", getName());
      return true;
    }

    for(Subscriber dependent: dependents) {
      logger.info("%s subscriber checking canProcess for dependent subscriber", getName(), dependent.getName());
      if(!dependent.isMessageProcessed(messageId)) {
        logger.warn("%s subscriber checking canProcess but dependent %s did not processed message yet so got negative response", getName(), dependent.getName());
        return false;
      }
    }
    logger.info("%s subscriber checked canProcess and get positive response as all dependent already processed this message", getName());
    return true;
  }

  @Override
  public List<String> getProcessPatterns() {
    return processPatterns;
  }

  @Override
  public void onMessageReceived(Message message) {
    if(!this.canProcess(message.getId())) {
      logger.warn("Subscriber %s is  waiting for it's dependents to process the message %n%s", this.getName(), message.getPayload().toString());
      return;
    }
    if(isMatching(message)) {
      try {
        messageProcessor.process(message, this);
        message.resetRetryCount();
        this.processedMessageIds.add(message.getId());
      } catch (MessageProcessorException e) {
        message.incrementRetryCount();
      }
    } else {
      String patterns = String.join(", ", processPatterns);
      if(patterns.length() > 0) {
        logger.info("Subscriber %s unable to matched any of provided pattern %s for below message %n%s", getName(), patterns, message.getPayload().toString());
      }
    }
    messageQueue.incrementProcessedMessageCount();
    currentOffset ++;
  }
  private boolean isMatching(Message message) {
    // Check if the message topic and content match the subscribed topics and process patterns
    return processPatterns == null || processPatterns.stream().anyMatch(pattern -> message.getPayload().toString().matches(pattern));
  }
}
