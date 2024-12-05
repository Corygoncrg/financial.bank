package com.example.security.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicConfig {

    @Bean
    public NewTopic securitySaveValidatorRequestTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_SECURITY_REQUEST_SAVE_VALIDATOR")
                .partitions(2)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic securitySaveValidatorResponseTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_SECURITY_RESPONSE_SAVE_VALIDATOR")
                .partitions(2)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic securityDeleteValidatorRequestTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_SECURITY_REQUEST_DELETE_VALIDATOR")
                .partitions(2)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic securityDeleteValidatorResponseTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_SECURITY_RESPONSE_DELETE_VALIDATOR")
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic securityValidatorRequestTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_SECURITY_REQUEST_VALIDATOR")
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic securityValidatorResponseTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_SECURITY_RESPONSE_VALIDATOR")
                .partitions(2)
                .replicas(1)
                .build();
    }
}
