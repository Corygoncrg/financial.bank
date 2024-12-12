package com.example.security.kafka.config;

import com.example.shared.kafka.TopicBuilderWrapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class KafkaTopicConfig {

    private final TopicBuilderWrapper topicBuilderWrapper = new TopicBuilderWrapper(3, 1);


    @Bean
    public NewTopic securitySaveValidatorRequestTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_SECURITY_REQUEST_SAVE_VALIDATOR");
    }

    @Bean
    public NewTopic securitySaveValidatorResponseTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_SECURITY_RESPONSE_SAVE_VALIDATOR");
    }

    @Bean
    public NewTopic securityDeleteValidatorRequestTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_SECURITY_REQUEST_DELETE_VALIDATOR");
    }

    @Bean
    public NewTopic securityDeleteValidatorResponseTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_SECURITY_RESPONSE_DELETE_VALIDATOR");
    }

    @Bean
    public NewTopic securityValidatorRequestTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_SECURITY_REQUEST_VALIDATOR");
    }

    @Bean
    public NewTopic securityValidatorResponseTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_SECURITY_RESPONSE_VALIDATOR");
    }
    @Bean
    public NewTopic securityValidatorRequestKeyTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_SECURITY_REQUEST_VALIDATOR_KEY");
    }
    @Bean
    public NewTopic securityValidatorResponseKeyTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_SECURITY_RESPONSE_VALIDATOR_KEY");
    }
    @Bean
    public NewTopic securityValidatorRequestRebuildKeyTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_SECURITY_REQUEST_REBUILD_VALIDATOR");
    }
    @Bean
    public NewTopic securityValidatorResponseRebuildKeyTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_SECURITY_RESPONSE_REBUILD_VALIDATOR");
    }

}
