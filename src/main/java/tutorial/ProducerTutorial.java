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

import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageBuilder;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerConfiguration;
import org.apache.pulsar.client.api.PulsarClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerTutorial {

    private static final String SERVICE_URL = "pulsar://localhost:6650";

    private static final String TOPIC_NAME = "persistent://sample/standalone/ns1/tutorial-topic";

    public static void main(String[] args) throws IOException {
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
    }

    private static final Logger log = LoggerFactory.getLogger(ProducerTutorial.class);
}
