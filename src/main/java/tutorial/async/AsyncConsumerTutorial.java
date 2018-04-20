package tutorial.async;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class AsyncConsumerTutorial {
    private static final Logger log = LoggerFactory.getLogger(AsyncConsumerTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";
    private static final String SUBSCRIPTION_NAME = "tutorial-subscription";

    private static void handleMessage(Consumer<byte[]> consumer, Message<byte[]> msg) {
        String msgContent = new String(msg.getData());
        String msgId = new String(msg.getMessageId().toByteArray());
        log.info("Received message '{}' with msg-id={}", msgContent, msgId);
        consumer.acknowledgeAsync(msg).thenRun(() -> {});
    }

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
                                .handle((msg, ex) -> {
                                   if (ex != null) {
                                       ex.printStackTrace();
                                   }

                                   log.info("Successfully received message with an ID of \"{}\" and a payload of \"{}\"",
                                           new String(msg.getMessageId().toByteArray()),
                                           new String(msg.getData()));
                                   return null;
                                });
                    } while (true);
                })
                .exceptionally(ex -> {
                    log.error(ex.getMessage());
                    return null;
                });
    }
}
