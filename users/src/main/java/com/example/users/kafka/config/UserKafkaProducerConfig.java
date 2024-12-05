package com.example.users.kafka.config;

import com.example.shared.kafka.config.KafkaProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserKafkaProducerConfig extends KafkaProducerConfig {

    public UserKafkaProducerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        super(bootstrapServers);
    }
}
