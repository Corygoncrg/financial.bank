package com.example.transactions.kafka;

import com.example.shared.kafka.TopicBuilderWrapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicConfig {

    private final TopicBuilderWrapper topicBuilderWrapper = new TopicBuilderWrapper(5, 2);


    @Bean
    public NewTopic transactionsRequestTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_TRANSACTIONS_REQUEST");
    }

    @Bean
    public NewTopic transactionsResponseTopic() {
        return topicBuilderWrapper.build("FINANCIAL_BANK_TRANSACTIONS_RESPONSE");
    }

}
