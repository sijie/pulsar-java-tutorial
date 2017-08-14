---
title: Pulsar Java Tutorial.
description: Learn how to create Pulsar applications in Java.
tags:
- pulsar
- Pulsar example
- Pulsar java
- Producer
- Consumer
---

# Getting started with Pulsar in Java

In this tutorial, we will create a simple Java application that can send and receive
messages through a [Pulsar](https://pulsar.incubator.apache.org/) topic.

We will start a local Pulsar cluster, use Maven to build a Pulsar application and
test it on the live cluster.

## Start Pulsar standalone

A very convenient way to start playing with Pulsar is to start a standalone service. That includes
a complete Pulsar-cluster in a single JVM instance.

There are few ways to get a Pulsar standalone cluster up and running:
 * Using binary releases: [Local cluster documentation](https://pulsar.incubator.apache.org/docs/latest/getting-started/LocalCluster/)
 * Using Docker: [Pulsar in Docker](https://pulsar.incubator.apache.org/docs/latest/getting-started/docker/)

If you are in MacOS, you can even use [Homebrew](https://brew.sh/) to install a local Pulsar cluster
for testing purposes:

```shell
# Register tap
$ brew tap streamlio/homebrew-formulae

# Install
brew install streamlio/homebrew-formulae/pulsar

# Start the Pulsar standalone as a background service
brew services start pulsar
```

## Clone the project

From the command line, use the following Git command to clone this project:

```bash
$ git clone https://github.com/streamlio/pulsar-java-tutorial.git
```

This command creates a directory named `pulsar-java-tutorial` at the current location, which contains
a basic Pulsar client Java project that you can build using Maven. The `pulsar-java-tutorial` directory
contains the following items:

File or directory | Description
:-----------------|:-----------
`pom.xml` | Contains settings for the Maven project
`src/main/java/tutorial` | Contains the application code
`src/main/resources` | Contains the [log4j](https://logging.apache.org/log4j/) logging configuration for the application


You can compile the project code using:

```
$ mvn clean package
```


## Consumer

The first step is to start a consumer. This will create a subscription and make sure that every
message published after that is going to be retained by Pulsar, until an explicit acknowlegment
of the message processing is received.

The consumer code is available at [src/main/java/tutorial/ConsumerTutorial.java](src/main/java/tutorial/ConsumerTutorial.java).
The sample code is very simple:

```java
// Create a Pulsar client instance. A single instance can be shared across many
// producers and consumer within the same application
PulsarClient client = PulsarClient.create(SERVICE_URL);

// Here you get the chance to configure consumer specific settings. eg:
ConsumerConfiguration conf = new ConsumerConfiguration();

// Allow multiple consumers to attache to the same subscription
// and get messages dispatched as a Queue
conf.setSubscriptionType(SubscriptionType.Shared);

// Once the consumer is created, it can be used for the entire application life-cycle
Consumer consumer = client.subscribe(TOPIC_NAME, SUBSCRIPTION_NAME, conf);
log.info("Created Pulsar consumer");

while (true) {
    // Wait until a message is available
    Message msg = consumer.receive();

    // Do something with the message
    String content = new String(msg.getData());
    log.info("Received message '{}' with msg-id={}", content, msg.getMessageId());

    // Acknowledge processing of message so that it can be deleted
    consumer.acknowledge(msg);
}
```

You can start the consumer from you IDE or directly from Maven with:

```shell
mvn exec:java -Dexec.mainClass="tutorial.ConsumerTutorial"
```


## Producer

The next step is to start the producer. In this example, we will start ingesting sample data into a topic where the consumer has already created a subscription. The producer code is available at [src/main/java/tutorial/ProducerTutorial.java](src/main/java/tutorial/ProducerTutorial.java) :

```java
// Create a Pulsar client instance. A single instance can be shared across many
// producers and consumer within the same application
PulsarClient client = PulsarClient.create(SERVICE_URL);

// Here you get the chance to configure producer specific settings. eg:
ProducerConfiguration conf = new ProducerConfiguration();

// Enable compression
conf.setCompressionType(CompressionType.LZ4);

// Once the producer is created, it can be used for the entire application life-cycle
Producer producer = client.createProducer(TOPIC_NAME, conf);
log.info("Created Pulsar producer");

// Send few test messages
for (int i = 0; i < 10; i++) {
    String content = String.format("hello-pulsar-%d", i);

    // Build a message object
    Message msg = MessageBuilder.create().setContent(content.getBytes()).build();

    // Send a message (waits until the message is persisted)
    MessageId msgId = producer.send(msg);

    log.info("Published msg='{}' with msg-id={}", content, msgId);
}

client.close();
```

You can start the consumer from you IDE or directly from Maven with:

```shell
$ mvn exec:java -Dexec.mainClass="tutorial.ProducerTutorial"
```

Once the producer starts ingestion of data, you should see the consumer receiving the published
sample messages.


You can find more documentation for Pulsar Java client at https://pulsar.incubator.apache.org/docs/latest/clients/Java/
 and the complete Javadoc API reference at https://pulsar.incubator.apache.org/api/client/.
