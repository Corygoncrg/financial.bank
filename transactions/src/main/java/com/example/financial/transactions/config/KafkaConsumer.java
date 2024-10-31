package com.example.financial.transactions.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "FINANCIAL_BANK_USERS", groupId = "transaction-group")
    public void receiveUserId(Long userId) {
        // Store or use the received userId in the CSV upload process
        System.out.println("Received user ID: " + userId);
        // Implement logic to add the userId to the transaction data
    }
}
