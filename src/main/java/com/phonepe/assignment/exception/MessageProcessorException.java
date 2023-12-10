package com.phonepe.assignment.exception;

public class MessageProcessorException extends Exception {

  public MessageProcessorException(Exception e) {
    super("Error occurred during message processing: " + e.getMessage(), e);
  }
}
