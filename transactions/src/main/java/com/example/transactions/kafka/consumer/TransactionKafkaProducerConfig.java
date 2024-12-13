package com.example.transactions.kafka.consumer;

import com.example.shared.kafka.config.KafkaProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class TransactionKafkaProducerConfig extends KafkaProducerConfig {

    private static final String PREFIX = "transactions";

    public TransactionKafkaProducerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        super(bootstrapServers);
    }

    @Bean(name = PREFIX + "ProducerConfig")
    public Map<String, Object> securityProducerConfig() {
        return super.producerConfig();
    }

    @Bean(name = PREFIX + "ProducerFactory")
    public ProducerFactory<String, Object> securityProducerFactory() {
        return super.producerFactory(securityProducerConfig());
    }

    @Bean(name = PREFIX + "KafkaTemplate")
    public KafkaTemplate<String, Object> securityKafkaTemplate() {
        return super.kafkaTemplate(securityProducerFactory());
    }
}
