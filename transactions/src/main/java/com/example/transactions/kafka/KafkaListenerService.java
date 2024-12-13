package com.example.transactions.kafka;

import com.example.shared.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    @Autowired
    private KafkaResponseHandler responseHandler;

    @KafkaListener(topics = "FINANCIAL_BANK_TRANSACTIONS_RESPONSE", groupId = "transaction-group-id", containerFactory = "userDtoKafkaListenerContainerFactory")
    public void listen(UserDto userDto) {
        System.out.println("Received User from Kafka: " + userDto);
        responseHandler.setUserDto(userDto);
    }
}
