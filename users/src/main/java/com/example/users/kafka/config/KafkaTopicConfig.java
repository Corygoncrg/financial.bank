package com.example.users.kafka.config;

import com.example.shared.kafka.TopicBuilderWrapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    private final TopicBuilderWrapper topicBuilderWrapper = new TopicBuilderWrapper(2, 1);

    @Bean
    public NewTopic usersRequestTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_USERS_REQUEST");
    }

    @Bean
    public NewTopic usersRequestDtoTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_USERS_REQUEST_DTO");
    }

    @Bean
    public NewTopic usersResponseTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_USERS_RESPONSE");
    }

    @Bean
    public NewTopic usersResponseDtoTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_USERS_RESPONSE_DTO");
    }
}
