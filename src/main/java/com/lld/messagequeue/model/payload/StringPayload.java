package com.lld.messagequeue.model.payload;

public class StringPayload<T> implements Payload<String> {
  String payload;

  public StringPayload(String message) {
    this.payload = message;
  }

  @Override
  public String getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    return payload;
  }
}
