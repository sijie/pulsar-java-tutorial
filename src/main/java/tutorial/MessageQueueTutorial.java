package tutorial;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class MessageQueueTutorial {
    private static final Logger LOG = LoggerFactory.getLogger(MessageQueueTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";
    private static final String SUBSCRIPTION_NAME = "tutorial-subscription";
    private static final int NUM_CONSUMERS = 5;

    private static class MQListener implements MessageListener<byte[]> {
        private long serialVersionUID = 1;

        @Override
        public void received(Consumer<byte[]> consumer, Message<byte[]> msg) {
            LOG.info("Message processed by consumer {}", consumer);
            try {
                consumer.acknowledge(msg);
            } catch (PulsarClientException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws PulsarClientException {
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(SERVICE_URL)
                .build();

        ConsumerBuilder<byte[]> consumerBuilder = client.newConsumer()
                .topic(TOPIC_NAME)
                .subscriptionName(SUBSCRIPTION_NAME)
                .subscriptionType(SubscriptionType.Shared);

        IntStream.range(0, NUM_CONSUMERS).forEach(i -> {
            try {
                consumerBuilder
                        .consumerName(String.format("mq-consumer-%d", i))
                        .messageListener(new MQListener())
                        .subscribe();
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        });
    }
}
