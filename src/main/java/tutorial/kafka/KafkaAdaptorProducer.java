package tutorial.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.stream.IntStream;

public class KafkaAdaptorProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaAdaptorProducer.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";

    public static void main(String[] args) {
        String topic = Utils.getTopicName(args);

        Properties props = new Properties();
        props.put("bootstrap.servers", SERVICE_URL);
        props.put("key.serializer", IntegerSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        Producer<Integer, String> producer = new KafkaProducer<>(props);

        log.info("Producer for topic {} successfully created", topic);

        IntStream.range(1, 101).forEach(i -> {
            String value = String.format("hello-%d", i);
            ProducerRecord<Integer, String> record = new ProducerRecord<>(topic, i, value);
            producer.send(record);
            log.info("Message with key {} sent successfully", record.key());
        });

        producer.close();
        log.info("Producer for topic {} successfully closed", topic);

        System.exit(0);
    }
}
