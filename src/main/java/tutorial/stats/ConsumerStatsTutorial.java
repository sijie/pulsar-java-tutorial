package tutorial.stats;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerStats;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
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

        do {
            Message<byte[]> msg = consumer.receive();

            // No processing logic applied, just basic acks
            consumer.acknowledge(msg);
        } while (!consumer.hasReachedEndOfTopic());

        ConsumerStats stats = consumer.getStats();

        log.info("Stats for this consumer");
        log.info("=======================");
        log.info("Messages received: {}", stats.getNumMsgsReceived());
        log.info("Receive rate: {}", stats.getRateMsgsReceived());
        log.info("Failed acks: {}", stats.getNumAcksFailed());

        consumer.close();
        client.close();
    }
}
