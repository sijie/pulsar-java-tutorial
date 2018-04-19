package tutorial.stats;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.ConsumerStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerStatsTutorial {
    private static final Logger log = LoggerFactory.getLogger(ConsumerStatsTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";
    private static final String SUBSCRIPTION_NAME = "tutorial-subscription";

    public static void main(String[] args) throws PulsarClientException {
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(SERVICE_URL)
                .build();

        log.info("Created a client for the Pulsar cluster at {}", SERVICE_URL);

        Consumer<byte[]> consumer = client.newConsumer()
                .topic(TOPIC_NAME)
                .subscriptionName(SUBSCRIPTION_NAME)
                .subscribe();

        log.info("Consumer created for topic {}", TOPIC_NAME);


        // Set a counter
        int i = 0;

        // Set a limit of messages to receive
        int limit = 500;

        // Receive the specified number of messages, then break the loop
        do {
            i++;
            Message<byte[]> msg = consumer.receive();

            // No processing logic applied, just basic message acks
            consumer.acknowledge(msg);
        } while (i < limit);

        ConsumerStats stats = consumer.getStats();

        log.info("Stats for this consumer");
        log.info("=======================");
        log.info("Messages received: {}", stats.getNumMsgsReceived());
        log.info("Failed acks: {}", stats.getNumAcksFailed());

        consumer.close();
        client.close();
    }
}
