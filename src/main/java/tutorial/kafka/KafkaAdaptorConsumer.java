package tutorial.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;

public class KafkaAdaptorConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaAdaptorConsumer.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String SUBSCRIPTION_NAME = "sub-1";

    public static void main(String[] args) {
        String topic = Utils.getTopicName(args);

        Properties props = new Properties();
        props.put("bootstrap.servers", SERVICE_URL);
        props.put("group.id", SUBSCRIPTION_NAME);
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", IntegerDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        Consumer<Integer, String> consumer = new KafkaConsumer<>(props);

        log.info("Consumer for topic {} successfully created", topic);

        consumer.subscribe(Collections.singleton(topic));

        log.info("Consumer successfully subscribed to topic {}", topic);

        do {
            consumer.

            ConsumerRecords<Integer, String> records = consumer.poll(100);

            records.forEach(record -> {
                log.info("Received record with a key of {} and a value of {}", record.key(), record.value());
            });

            consumer.commitSync();

            break;
        } while (true);
    }
}
