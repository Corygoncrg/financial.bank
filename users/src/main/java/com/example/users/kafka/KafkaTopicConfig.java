package com.example.users.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic usersTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_USERS")
                .build();
    }

    @KafkaListener(id = "myId", topics = "FINANCIAL_BANK_USERS")
    public void listen(String in) {
        System.out.println(in);
    }
}
