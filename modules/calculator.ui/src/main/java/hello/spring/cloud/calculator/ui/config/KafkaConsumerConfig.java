package hello.spring.cloud.calculator.ui.config;

import hello.spring.cloud.svc.ifw.runtime.interceptor.CounterInterceptor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private static final String GROUP_ID = "calculator-ui";
    private static final String KAFKA_BOOTSTRAP_SERVERS_CONFIG = "localhost:9092";

    private Map<String, Object> consumerProps() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS_CONFIG);
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return propsMap;
    }

    private ConsumerFactory<String, CounterInterceptor.CounterEvent> consumerFactory() {
        JsonDeserializer<CounterInterceptor.CounterEvent> jd = new JsonDeserializer<>(CounterInterceptor.CounterEvent.class);
        jd.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(consumerProps(), new StringDeserializer(), jd);
    }

    @Bean
    public KafkaTransactionManager kafkaTxMgrt(ProducerFactory<String, Map<String, Object>> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory(KafkaTransactionManager kafkaTxMgrt) {
        ConcurrentKafkaListenerContainerFactory<String, CounterInterceptor.CounterEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setTransactionManager(kafkaTxMgrt);
        return factory;
    }

}
