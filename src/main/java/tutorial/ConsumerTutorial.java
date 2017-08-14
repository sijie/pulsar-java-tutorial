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
import org.apache.pulsar.client.api.ConsumerConfiguration;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerTutorial {

    private static final String SERVICE_URL = "pulsar://localhost:6650";

    private static final String TOPIC_NAME = "persistent://sample/standalone/ns1/tutorial-topic";

    private static final String SUBSCRIPTION_NAME = "tutorial-subscription";

    public static void main(String[] args) throws IOException {
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
    }

    private static final Logger log = LoggerFactory.getLogger(ProducerTutorial.class);

}
