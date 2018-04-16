package tutorial.async;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncConsumerTutorial {
    private static final Logger log = LoggerFactory.getLogger(AsyncConsumerTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";
    private static final String SUBSCRIPTION_NAME = "tutorial-subscription";

    public static void main(String[] args) throws PulsarClientException {
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(SERVICE_URL)
                .build();

        log.info("Created a client for the Pulsar cluster running at {}", SERVICE_URL);

        client.newConsumer()
                .topic(TOPIC_NAME)
                .subscriptionName(SUBSCRIPTION_NAME)
                .subscriptionType(SubscriptionType.Shared)
                .subscribeAsync()
                .thenAccept(consumer -> {
                    log.info("Consumer created asynchronously for the topic {}", TOPIC_NAME);

                    do {
                        consumer.receiveAsync()
                                .thenAccept(msg -> {
                                    String msgContent = new String(msg.getData());
                                    String msgId = new String(msg.getMessageId().toByteArray());
                                    log.info("Received message '{}' with msg-id={}", msgContent, msgId);
                                })
                                .exceptionally(ex -> {
                                    log.error(ex.toString());
                                    return null;
                                });
                    } while (true);
                })
                .exceptionally(ex -> {
                    log.error(ex.toString());
                    return null;
                });
    }
}
