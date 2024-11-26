package com.example.users.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
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
    public NewTopic usersResponseTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_USERS_RESPONSE")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
