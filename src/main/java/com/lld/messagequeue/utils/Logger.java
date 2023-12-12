package com.lld.messagequeue.utils;

public interface Logger {
  void info(String message, Object... args);
  void warn(String message, Object... args);
  void error(String message, Throwable throwable);
}
