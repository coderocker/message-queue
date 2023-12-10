package com.phonepe.assignment.subscriber;

import com.phonepe.assignment.model.Message;

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
  List<Subscriber> getDependents();
  boolean canProcess(String messageId);
}
