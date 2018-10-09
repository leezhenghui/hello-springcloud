package hello.spring.cloud.calculator.ui.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfg {

    private static final String KAFKA_BOOTSTRAP_SERVERS_CONFIG = "localhost:9092";

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS_CONFIG);
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    @Bean
    public ProducerFactory<String, Map<String, Object>> producerFactory() {
        DefaultKafkaProducerFactory<String, Map<String, Object>> producerFactory = new DefaultKafkaProducerFactory<>(
                senderProps());
        producerFactory.setTransactionIdPrefix("txid-");
        return producerFactory;
    }

    @Bean
    public KafkaTemplate kafkaTemplate() {
        return new KafkaTemplate(producerFactory());
    }

    @Bean
    public ChainedTransactionManager chainedTxMgrt(KafkaTransactionManager<?, ?> kafkaTxMgrt) {
        return new ChainedTransactionManager(kafkaTxMgrt);
    }
}
