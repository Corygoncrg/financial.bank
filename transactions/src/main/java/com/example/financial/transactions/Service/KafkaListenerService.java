package com.example.financial.transactions.Service;

import com.example.financial.transactions.config.KafkaResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    @Autowired
    private KafkaResponseHandler responseHandler;

    @KafkaListener(topics = "FINANCIAL_BANK_USERS", groupId = "transactions-group")
    public void listen(Long userId) {
        System.out.println("Received User ID from Kafka: " + userId);
        responseHandler.setUserId(userId);
    }
}
