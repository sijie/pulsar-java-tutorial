package tutorial.subscriptions;

import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageBuilder;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class ExclusiveSubscriptionTutorial {
    private static final Logger LOG = LoggerFactory.getLogger(ExclusiveSubscriptionTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";
    private static final String SUBSCRIPTION_NAME = "tutorial-subscription";
    private static final SubscriptionType SUBSCRIPTION_TYPE = SubscriptionType.Exclusive;

    public static void main(String[] args) throws PulsarClientException {
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(SERVICE_URL)
                .build();

        Producer<byte[]> producer = client.newProducer()
                .topic(TOPIC_NAME)
                .create();

        ConsumerBuilder<byte[]> consumer1 = client.newConsumer()
                .topic(TOPIC_NAME)
                .subscriptionName(SUBSCRIPTION_NAME)
                .subscriptionType(SUBSCRIPTION_TYPE);

        ConsumerBuilder<byte[]> consumer2 = client.newConsumer()
                .topic(TOPIC_NAME)
                .subscriptionName(SUBSCRIPTION_NAME)
                .subscriptionType(SUBSCRIPTION_TYPE);

        IntStream.range(0, 999).forEach(i -> {
            Message<byte[]> msg = MessageBuilder.create()
                    .setContent(String.format("message-%d", i).getBytes())
                    .build();
            try {
                producer.send(msg);
            } catch (PulsarClientException e) {
                LOG.error(e.getMessage());
            }
        });

        // Consumer 1 can subscribe to the topic
        consumer1.subscribe();

        // But oops! Consumer 2 cannot due to the exclusive subscription held by consumer 1
        consumer2.subscribeAsync()
                .handle((consumer, exception) -> {
                    LOG.error(exception.getMessage());
                    return null;
                });
    }
}
