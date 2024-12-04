package com.example.users.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic usersRequestTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_USERS_REQUEST")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic usersRequestDtoTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_USERS_REQUEST_DTO")
                .partitions(3)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic usersRequestAuthTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_USERS_REQUEST_AUTH")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic usersResponseTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_USERS_RESPONSE")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic usersResponseDtoTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_USERS_RESPONSE_DTO")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic usersResponseAuthTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_USERS_RESPONSE_AUTH")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
