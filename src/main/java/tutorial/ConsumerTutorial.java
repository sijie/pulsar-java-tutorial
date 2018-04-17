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

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerTutorial {
    private static final Logger log = LoggerFactory.getLogger(ConsumerTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";
    private static final String SUBSCRIPTION_NAME = "tutorial-subscription";

    public static void main(String[] args) throws IOException {
        // Create a Pulsar client instance. A single instance can be shared across many
        // producers and consumer within the same application
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(SERVICE_URL)
                .build();

        // Here you get the chance to configure consumer specific settings. eg:
        Consumer<byte[]> consumer = client.newConsumer()
                .topic(TOPIC_NAME)
                // Allow multiple consumers to attach to the same subscription
                // and get messages dispatched as a queue
                .subscriptionType(SubscriptionType.Shared)
                .subscriptionName(SUBSCRIPTION_NAME)
                .subscribe();


        // Once the consumer is created, it can be used for the entire application lifecycle
        log.info("Created consumer for the topic {}", TOPIC_NAME);

        do {
            // Wait until a message is available
            Message<byte[]> msg = consumer.receive();

            // Extract the message as a printable string and then log
            String content = new String(msg.getData());
            log.info("Received message '{}' with ID {}", content, msg.getMessageId());

            // Acknowledge processing of the message so that it can be deleted
            consumer.acknowledge(msg);
        } while (true);
    }
}
