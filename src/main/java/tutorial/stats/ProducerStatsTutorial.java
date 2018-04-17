package tutorial.stats;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageBuilder;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerStats;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class ProducerStatsTutorial {
    private static final Logger log = LoggerFactory.getLogger(ProducerStatsTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";
    private static final int NUM_TO_PRODUCE = 1000;

    public static void main(String[] args) throws PulsarClientException {
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(SERVICE_URL)
                .build();

        Producer<byte[]> producer = client.newProducer()
                .topic(TOPIC_NAME)
                .create();

        IntStream.range(1, NUM_TO_PRODUCE + 1).forEach(i -> {
            Message<byte[]> msg = MessageBuilder.create()
                    .setContent(String.format("Message %d", i).getBytes())
                    .build();
            try {
                MessageId msgId = producer.send(msg);
            } catch (PulsarClientException e) {
                log.error(e.getMessage());
            }
        });

        ProducerStats stats = producer.getStats();

        log.info("Stats for this producer:");
        log.info("========================");
        log.info("Messages sent: %d", stats.getTotalMsgsSent());
        log.info("Send rate: %d", stats.getSendMsgsRate());

        producer.close();
        client.close();
    }
}
