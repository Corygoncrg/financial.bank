package com.example.security.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicConfig {

    @Bean
    public NewTopic securityTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_SECURITY_REQUEST")
                .partitions(2)
                .replicas(1)
                .build();
    }
}
