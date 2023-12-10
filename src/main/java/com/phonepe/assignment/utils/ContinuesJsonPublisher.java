package com.phonepe.assignment.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;
import com.phonepe.assignment.model.payload.JsonPayload;
import com.phonepe.assignment.publisher.MessagePublisher;
import com.phonepe.assignment.publisher.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ContinuesJsonPublisher implements Runnable{
  private final static Faker faker = new Faker();
  private final List<Publisher<JsonNode>> publishers = new ArrayList<>();
  private final Random random;

  public ContinuesJsonPublisher(int numberOfPublishers) {
    random = new Random();
    for (int i = 0; i < numberOfPublishers; i++) {
      publishers.add(new MessagePublisher(generateRandomName()));
    }
  }

  private static String generateRandomName() {
    return faker.name().fullName();
  }

  public static String generateRandomJson() {
    ObjectMapper objectMapper = new ObjectMapper();

    ObjectNode jsonNode = objectMapper.createObjectNode();

    // Add random key-value pairs to the JSON object
    for (int i = 0; i < 5; i++) {
      String key = faker.lorem().word();
      String value = faker.lorem().sentence();
      jsonNode.put(key, value);
    }

    try {
      return objectMapper.writeValueAsString(jsonNode);
    } catch (Exception e) {
      e.printStackTrace();
      return "{}";
    }
  }

  @Override
  public synchronized void run() {
    while (true) {
      // Generate a random JSON message
      int randomIndex = random.nextInt(publishers.size());
      String jsonString = generateRandomJson();

      try {
        publishers.get(randomIndex).publish(new JsonPayload(jsonString));

        // Sleep for a short duration before publishing the next message
        Thread.sleep(10000);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
