package tutorial.async;

import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.MessageBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class AsyncProducerTutorial {
    private static final Logger log = LoggerFactory.getLogger(AsyncProducerTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";

    public static void main(String[] args) {
        try {
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(SERVICE_URL)
                    .build();

            log.info("Created a client for the Pulsar cluster running at {}", SERVICE_URL);

            client.newProducer()
                    .topic(TOPIC_NAME)
                    .compressionType(CompressionType.LZ4)
                    .createAsync()
                    .thenAccept(producer -> {
                        log.info("Producer created asynchronously for the topic {}", TOPIC_NAME);

                        MessageBuilder<byte[]> msgBuilder = MessageBuilder.create();

                        // Send 10 messages with varying content
                        IntStream.range(1, 11).forEach(i -> {
                            byte[] msgContent = String.format("hello-pulsar-%d", i).getBytes();
                            msgBuilder.setContent(msgContent);
                            producer.sendAsync(msgBuilder.build())
                                    .thenAccept(msgId -> {
                                        log.info("Successfully sent message with ID {}", msgId);
                                    })
                                    .exceptionally(ex -> {
                                        ex.printStackTrace();
                                        return null;
                                    });
                        });
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }
}
