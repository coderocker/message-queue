package com.lld.messagequeue.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lld.messagequeue.model.payload.Payload;
import com.lld.messagequeue.publisher.Publisher;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Getter
public class JsonMessage implements Message {

  private final String id;
  private final Publisher<JsonNode> publisher;
  private final Payload<JsonNode> payload;
  private final Instant creationTime;
  private int retryCount = 0;
  private int publishCount =0;


  public JsonMessage(Publisher<JsonNode> publisher, Payload<JsonNode> payload) {
    this.publisher = publisher;
    this.id = UUID.randomUUID().toString();
    this.payload = payload;
    this.creationTime = Instant.now();
  }

  @Override
  public void incrementRetryCount() {
    retryCount++;
  }

  @Override
  public void resetRetryCount() {
    retryCount = 0;
  }

  @Override
  public void incrementPublishCount() {
    publishCount++;
  }


  @Override
  public Instant getExpiry(Duration ttl) {
    return creationTime.plus(ttl);
  }

  @Override
  public boolean isExpired(Duration ttl) {
    return getExpiry(ttl).isBefore(Instant.now());
  }

  @Override
  public String toString() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper.valueToTree(this).toPrettyString();
  }
}
