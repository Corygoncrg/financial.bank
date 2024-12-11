package com.example.security.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicConfig {

    @Bean
    public NewTopic securitySaveValidatorRequestTopic() {
        return topicBuilder("FINANCIAL_BANK_SECURITY_REQUEST_SAVE_VALIDATOR");
    }

    @Bean
    public NewTopic securitySaveValidatorResponseTopic() {
        return topicBuilder("FINANCIAL_BANK_SECURITY_RESPONSE_SAVE_VALIDATOR");
    }

    @Bean
    public NewTopic securityDeleteValidatorRequestTopic() {
        return topicBuilder("FINANCIAL_BANK_SECURITY_REQUEST_DELETE_VALIDATOR");
    }

    @Bean
    public NewTopic securityDeleteValidatorResponseTopic() {
        return topicBuilder("FINANCIAL_BANK_SECURITY_RESPONSE_DELETE_VALIDATOR");
    }

    @Bean
    public NewTopic securityValidatorRequestTopic() {
        return topicBuilder("FINANCIAL_BANK_SECURITY_REQUEST_VALIDATOR");
    }

    @Bean
    public NewTopic securityValidatorResponseTopic() {
        return topicBuilder("FINANCIAL_BANK_SECURITY_RESPONSE_VALIDATOR");
    }
    @Bean
    public NewTopic securityValidatorRequestKeyTopic() {
        return topicBuilder("FINANCIAL_BANK_SECURITY_REQUEST_VALIDATOR_KEY");
    }
    @Bean
    public NewTopic securityValidatorResponseKeyTopic() {
        return topicBuilder("FINANCIAL_BANK_SECURITY_RESPONSE_VALIDATOR_KEY");
    }

    private static NewTopic topicBuilder(String topic) {
        return TopicBuilder.name(topic)
                .partitions(2)
                .replicas(1)
                .build();
    }
}
