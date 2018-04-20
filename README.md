# Getting started with Pulsar in Java

This repo houses a number of examples for the [Pulsar Java client](http://pulsar.incubator.apache.org/docs/latest/clients/Java/). You can see example code in [`src/main/java/tutorial`](src/main/java/tutorial). Each of these files is a runnable example.

> You can access full Javadoc for the `pulsar-client` library [here](https://pulsar.incubator.apache.org/api/client/).

## Running Pulsar

First, you'll need to clone this repo locally:

```bash
$ git clone https://github.com/streamlio/pulsar-java-tutorial && \
  cd pulsar-java-tutorial
```

The repo contains the following:

File or directory | Description
:-----------------|:-----------
`build.gradle` | Contains settings for the Gradle project
`src/main/java/tutorial` | Contains the application code
`src/main/resources` | Contains the [log4j](https://logging.apache.org/log4j/) logging configuration for the application

### Running Pulsar

In order to run these examples, you'll need to install Pulsar and run it in [standalone mode](http://pulsar.incubator.apache.org/docs/latest/getting-started/LocalCluster/) on your machine. Instructions for [macOS](#macos) and [Linux](#linux) are directly below.

> **Standalone mode** for Pulsar means that all necessary components---[BookKeeper bookies](http://bookkeeper.apache.org/docs/latest/getting-started/concepts/#bookies), a [Pulsar broker](http://pulsar.incubator.apache.org/docs/latest/getting-started/ConceptsAndArchitecture/#brokers), and a [ZooKeeper quorum](https://zookeeper.apache.org)---run in a single JVM process.

#### macOS

You can install and run Pulsar using [Homebrew](https://brew.sh):

```bash
# Register tap
$ brew tap streamlio/homebrew-formulae

# Install
$ brew install streamlio/homebrew-formulae/pulsar

# Start the Pulsar standalone as a background service
$ brew services start pulsar
```

#### Linux

For instructions on setting up Pulsar and running a standalone cluster on Linux, see the [official Pulsar documentation](http://pulsar.incubator.apache.org/docs/latest/getting-started/LocalCluster/).

> You can also run Pulsar [using Docker](https://pulsar.incubator.apache.org/docs/latest/getting-started/docker/).

## Examples

The table below lists the examples contained in this repo as well as the command that you'll need to run to start up that example (once you have [Pulsar running](#running-pulsar)):

Example | Description | Class | Command to run
:-------|:------------|:------|:--------------
Pulsar producer | A simple Pulsar producer that produces 10 messages on a Pulsar topic | [`ProducerTutorial`](src/main/java/tutorial/ProducerTutorial.java) | `./gradlew producerTutorial`
Pulsar consumer | A simple Pulsar consumer that listens indefinitely on a Pulsar topic for incoming messages | [`ConsumerTutorial`](src/main/java/tutorial/ConsumerTutorial.java) | `./gradlew consumerTutorial`
Kafka adaptor producer | A Kafka producer that uses the [Kafka client wrapper](http://pulsar.incubator.apache.org/docs/latest/adaptors/KafkaWrapper/) to communicate with Pulsar | [`KafkaAdaptorProducer`](src/main/java/tutorial/kafka/KafkaAdaptorProducer.java) | `./gradlew kafkaProducerTutorial`
Kafka adaptor consumer | A Kafka consumer that uses the Kafka client wrapper to communicate with Pulsar | [`KafkaAdaptorConsumer`](src/main/java/tutorial/kafka/KafkaAdaptorConsumer.java) | `./gradlew kafkaConsumerTutorial`

### Basic consumer and producer

To start up a basic Pulsar Java consumer listening on the topic `tutorial-topic`:

```bash
$ ./gradlew consumerTutorial
```

If Pulsar is running, you should see something like this in logs when the consumer has successfully subscribed to the topic:

```log
09:55:40.711 [main] INFO  tutorial.ConsumerTutorial - Created consumer for the topic tutorial-topic
```

Now you can start up a basic Java producer on `tutorial-topic` in another terminal window:

```bash
$ ./gradlew producerTutorial
```

This producer will send 10 messages on the topic (`hello-pulsar-1`, `hello-pulsar-2`, etc.). In the terminal window for the Java consumer, you should see `INFO`-level logs like this for each message received:

```log
09:57:01.447 [main] INFO  tutorial.ConsumerTutorial - Received message 'hello-pulsar-1' with ID 29:24:-1:0
```

### Kafka consumer and producer

Pulsar offers a [Kafka client wrapper](http://pulsar.incubator.apache.org/docs/latest/adaptors/KafkaWrapper/) for Java that enables you to use your existing Kafka applications with Pulsar. To start up the Kafka consumer:

```bash
$ ./gradlew kafkaConsumerTutorial
```

You should see this in the logs when the consumer successfully subscribes to the topic `tutorial-topic`:

```log
10:46:51.648 [main] INFO  tutorial.kafka.KafkaAdaptorConsumer - Consumer successfully subscribed to topics [topic]
```

The consumer will then poll for (and process) 100 messages at a time. Whenever it finishes processing 100 messages it will re-subscribe to `tutorial-topic` and begin processing messages again. To start up a producer in a different terminal window:

```bash
$ ./gradlew kafkaProducerTutorial
```

The producer will log successful message sends like so:

```log
10:43:26.408 [main] INFO  tutorial.kafka.KafkaAdaptorProducer - Message with key 979 sent successfully
```

Meanwhile, the consumer will log each successfully received message:

```log
11:01:19.998 [main] INFO  tutorial.kafka.KafkaAdaptorConsumer - Received record with a key of 6 and a value of hello-6
```