package com.example.security.kafka.config;

import com.example.shared.kafka.config.KafkaProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityKafkaProducerConfig extends KafkaProducerConfig {

    public SecurityKafkaProducerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        super(bootstrapServers);
    }
}
