package com.example.financial.transactions.kafka;

import com.example.financial.transactions.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    @Autowired
    private KafkaResponseHandler responseHandler;

    @KafkaListener(topics = "FINANCIAL_BANK_USERS", groupId = "transactions-group")
    public void listen(UserDto userDto) {
        System.out.println("Received User ID from Kafka: " + userDto);
        responseHandler.setUserDto(userDto);
    }
}
