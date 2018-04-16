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

public class ConsumerTutorial {
    private static final Logger log = LoggerFactory.getLogger(ConsumerTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "persistent://sample/standalone/ns1/tutorial-topic";
    private static final String SUBSCRIPTION_NAME = "tutorial-subscription";

    public static void main(String[] args) throws IOException {
        // Create a Pulsar client instance. A single instance can be shared across many
        // producers and consumer within the same application
        ClientBuilder clientBuilder = PulsarClient.builder();
        clientBuilder.serviceUrl(SERVICE_URL);
        PulsarClient client = clientBuilder.build();

        // Here you get the chance to configure consumer specific settings. eg:
        ConsumerBuilder<byte[]> consumerBuilder = client.newConsumer();
        consumerBuilder.topic(TOPIC_NAME)
                // Allow multiple consumers to attache to the same subscription
                // and get messages dispatched as a Queue
                .subscriptionType(SubscriptionType.Shared)
                .subscriptionName(SUBSCRIPTION_NAME);


        // Once the consumer is created, it can be used for the entire application life-cycle
        Consumer<byte[]> consumer = consumerBuilder.subscribe();
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
    }
}
