package tutorial.schema;

import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class SchemaProducerTutorial {
    private static final Logger log = LoggerFactory.getLogger(SchemaProducerTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";
    private static final int NUM_TO_PRODUCE = 1000;

    public static void main(String[] args) throws PulsarClientException {
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(SERVICE_URL)
                .build();

        log.info("Created a client for the Pulsar cluster at {}", SERVICE_URL);

        Producer<Tweet> producer = client.newProducer(new TweetSchema())
                .topic(TOPIC_NAME)
                .compressionType(CompressionType.LZ4)
                .create();

        log.info("Created a tweet producer for the topic {}", TOPIC_NAME);

        IntStream.range(1, NUM_TO_PRODUCE + 1).forEach(i -> {
            Tweet tweet = new Tweet("elonmusk123", String.format("This is tweet %d", i));

            try {
                MessageId msgId = producer.send(tweet);
                log.info("Successfully sent tweet message with an ID of {}", msgId);
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        });
    }
}