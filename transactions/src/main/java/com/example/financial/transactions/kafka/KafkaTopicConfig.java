package com.example.financial.transactions.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicConfig {

    @Bean
    public NewTopic transactionsRequestTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_TRANSACTIONS_REQUEST")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic transactionsResponseTopic() {
        return TopicBuilder.name("FINANCIAL_BANK_TRANSACTIONS_RESPONSE")
                .partitions(3)
                .replicas(1)
                .build();
    }

}
