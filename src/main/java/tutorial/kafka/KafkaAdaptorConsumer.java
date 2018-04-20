package tutorial.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

public class KafkaAdaptorConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaAdaptorConsumer.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String SUBSCRIPTION_NAME = "sub-1";

    private static class ConsumerLoop implements Runnable {
        private final Consumer<Integer, String> consumer;
        private final Collection<String> topics;

        ConsumerLoop(Consumer<Integer, String> consumer, Collection<String> topics) {
            this.consumer = consumer;
            this.topics = topics;
        }

        @Override
        public void run() {
            try {
                consumer.subscribe(topics);

                log.info("Consumer successfully subscribed to topics {}", topics);

                ConsumerRecords<Integer, String> records = consumer.poll(Long.MAX_VALUE);
                records.forEach(record -> {
                    log.info("Received record with a key of {} and a value of {}", record.key(), record.value());
                });
            } catch (WakeupException e) {
                // Ignore
            } finally {
                consumer.commitSync();
                log.info("Consumer for topics {} temporarily closed", topics);
                this.run();
            }
        }
    }

    public static void main(String[] args) {
        String topic = Utils.getTopicName(args);

        Properties props = new Properties();
        props.put("bootstrap.servers", SERVICE_URL);
        props.put("group.id", SUBSCRIPTION_NAME);
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", IntegerDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        Consumer<Integer, String> consumer = new KafkaConsumer<>(props);

        new ConsumerLoop(consumer, Collections.singleton(topic)).run();
    }
}
