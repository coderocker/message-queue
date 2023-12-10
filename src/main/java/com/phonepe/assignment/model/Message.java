package com.phonepe.assignment.model;

import com.phonepe.assignment.model.payload.Payload;
import com.phonepe.assignment.publisher.Publisher;

import java.time.Duration;
import java.time.Instant;

public interface Message {
  int getRetryCount();
  void incrementRetryCount();
  void incrementPublishCount();
  int getPublishCount();
  void resetRetryCount();
  String getId();
  Payload getPayload();
  Instant getCreationTime();
  Instant getExpiry(Duration ttl);
  boolean isExpired(Duration ttl);
  Publisher getPublisher();
}
