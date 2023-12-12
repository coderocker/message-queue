# Message Queue System

## Overview

This is a simple implementation of a message queue system that allows publishers to send messages to a queue, and subscribers to consume messages from the queue. The system supports multiple subscribers with the ability to process messages based on patterns.

## Components

### MessageQueue

The `MessageQueue` class is the core component that manages the message queue. It handles message publishing, message dequeueing, and message expiration. It also includes a cleaner worker to remove expired messages.

### MessagePublisher

The `MessagePublisher` class represents a publisher that can publish messages to the message queue. It uses a unique ID and name for identification.

### MessageSubscriber

The `MessageSubscriber` class represents a subscriber that can consume messages from the message queue. It includes the ability to subscribe to specific patterns and process messages accordingly.

### SubscriberWorker

The `SubscriberWorker` class is a worker thread associated with each subscriber. It continuously consumes messages from the message queue and processes them.

### DeadLetterQueue

The `DeadLetterQueue` class is responsible for managing messages that exceed the maximum retry count. These messages are moved to the dead-letter queue.

### JsonMessageProcessor

The `JsonMessageProcessor` class is an example implementation of a message processor. It processes JSON messages received by subscribers.

### Other Classes

- `JsonMessage`: Represents a JSON message in the system.
- `JsonPayload`: Represents the payload of a JSON message.
- `CleanerWorker`: Handles the periodic cleaning of expired messages from a queue.
- `SimpleLogger`: Provides a simple logging mechanism for the system.

## Usage

To use the message queue system, you can create instances of `MessagePublisher` and `MessageSubscriber`. Publishers can publish messages, and subscribers can subscribe to specific patterns and process messages accordingly.

## Testing

The system includes test classes for various components, including `MessageSubscriberTest`, `MessagePublisherTest`, and `MessageQueueTest`. These tests cover different scenarios to ensure the correctness of the implementation.

## Dependencies

The system uses Java with the following dependencies:
- [Lombok](https://projectlombok.org/): Simplifies Java boilerplate code.
- [JUnit](https://junit.org/junit5/): Used for unit testing.

## Building and Running

The project can be built using your favorite build tool (e.g., Maven or Gradle). Ensure that the dependencies are resolved, and run the tests to verify the functionality.

