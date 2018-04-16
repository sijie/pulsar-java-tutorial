/**
 * Copyright 2017 Streamlio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tutorial;

import java.io.IOException;

import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerTutorial {

    private static final String SERVICE_URL = "pulsar://localhost:6650";

    private static final String TOPIC_NAME = "tutorial-topic";

    public static void main(String[] args) throws IOException {
        // Create a Pulsar client instance. A single instance can be shared across many
        // producers and consumer within the same application
        ClientBuilder clientBuilder = PulsarClient.builder();
        clientBuilder.serviceUrl(SERVICE_URL);
        PulsarClient client = clientBuilder.build();

        // Here you get the chance to configure producer specific settings. eg:
        ProducerBuilder<byte[]> producerBuilder = client.newProducer();

        // Enable compression
        producerBuilder.compressionType(CompressionType.LZ4);
        // Set the topic
        producerBuilder.topic(TOPIC_NAME);

        // Once the producer is created, it can be used for the entire application life-cycle
        Producer<byte[]> producer = producerBuilder.create();
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
    }

    private static final Logger log = LoggerFactory.getLogger(ProducerTutorial.class);
}
