package com.lld.messagequeue.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleLogger implements Logger {

  private static final Map<Class<?>, SimpleLogger> instances = new ConcurrentHashMap<>();

  private final String className;

  private SimpleLogger(Class<?> clazz) {
    this.className = clazz.getSimpleName();
  }

  private SimpleLogger(Class<?> clazz, boolean useFullyQualifiedClassName) {
    this.className = useFullyQualifiedClassName ? clazz.getName() : clazz.getSimpleName();
  }

  // Static method to get the singleton instance for a specific class
  public static SimpleLogger getInstance(Class<?> clazz) {
    return instances.computeIfAbsent(clazz, key -> new SimpleLogger(clazz));
  }

  // Static method to get the singleton instance for a specific class
  public static SimpleLogger getInstance(Class<?> clazz, boolean useFullyQualifiedClassName) {
    return instances.computeIfAbsent(clazz, key -> new SimpleLogger(clazz, useFullyQualifiedClassName));
  }


  private void log(String level, String message) {
    String timestamp = getTimestamp();
    String formattedMessage = String.format("[%s] : %s : [%s] : %s%n", className, timestamp, level, message);
    if (level.equals("ERROR")) {
      System.err.print(formattedMessage);
    }
    System.out.print(formattedMessage);
  }

  private String getTimestamp() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return dateFormat.format(new Date());
  }

  // Enhanced info and warn method that takes direct parameters
  private String format(String format, Object... args) {
    if (args.length > 0) {
      return String.format(format, args);
    } else {
      return format;
    }
  }

  private void info(String message) {
    log("INFO", message);
  }

  @Override
  public void info(String format, Object... args) {
    info(format(format, args));
  }

  private void warn(String message) {
    log("WARN", message);
  }

  @Override
  public void warn(String format, Object... args) {
    warn(format(format, args));
  }

  @Override
  public void error(String message, Throwable throwable) {
    log("ERROR", message);
    System.err.printf("ERROR: %s%n", message);
    if (throwable != null) {
      throwable.printStackTrace();
    }
  }
}
