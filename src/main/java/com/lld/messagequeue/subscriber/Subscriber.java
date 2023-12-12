package com.lld.messagequeue.subscriber;

import com.lld.messagequeue.model.Message;

import java.util.List;
import java.util.Set;

public interface Subscriber {
  String getName();
  String getId();
  List<String> getProcessPatterns();
  void onMessageReceived(Message message);
  int getCurrentOffset();
  void decrementCurrentOffset();
  void incrementCurrentOffset();
  void setCurrentOffset(int offset);
  void addDependents(List<Subscriber> subscribers);
  void addDependent(Subscriber subscriber);
  Set<String> getProcessedMessageIds();
  boolean isMessageProcessed(String id);
  void removeProcessedMessage(String id);
  List<Subscriber> getDependents();
  boolean canProcess(String messageId);
}
