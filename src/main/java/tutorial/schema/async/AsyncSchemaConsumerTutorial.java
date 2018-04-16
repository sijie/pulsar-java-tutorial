package tutorial.schema.async;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tutorial.schema.Tweet;
import tutorial.schema.TweetSchema;

public class AsyncSchemaConsumerTutorial {
    private static final Logger log = LoggerFactory.getLogger(AsyncSchemaConsumerTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";
    private static final String SUBSCRIPTION_NAME = "tutorial-subscription";

    public static void main(String[] args) {
        try {
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(SERVICE_URL)
                    .build();

            log.info("Created a client for the Pulsar cluster running at {}", SERVICE_URL);

            client.newConsumer(new TweetSchema())
                    .topic(TOPIC_NAME)
                    .subscriptionName(SUBSCRIPTION_NAME)
                    .subscriptionType(SubscriptionType.Shared)
                    .subscribeAsync()
                    .thenAccept((Consumer<Tweet> tweetConsumer) -> {
                        log.info("Consumer created asynchronously for the topic {}", TOPIC_NAME);

                        do {
                            tweetConsumer.receiveAsync()
                                    .thenAccept((Message<Tweet> tweetMsg) -> {
                                        Tweet tweet = tweetMsg.getValue();
                                        String username = tweet.getUsername();
                                        String content = tweet.getContent();
                                        String timestamp = tweet.getTimestamp();
                                        log.info("The user {} just tweeted: \"{}\" at {}", username, content, timestamp);
                                        tweetConsumer.acknowledgeAsync(tweetMsg)
                                                .thenRun(() -> log.info("Acknowledged tweet message with ID {}", tweetMsg.getMessageId()));
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
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }
}
