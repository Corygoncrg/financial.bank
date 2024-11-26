package com.example.security.kafka;

import com.example.security.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    @Autowired
    private KafkaDtoResponseHandler dtoHandler;

    @KafkaListener(topics = "FINANCIAL_BANK_USERS_RESPONSE", groupId = "transactions-group")
    public void listen(UserDto userDto) {
        System.out.println("Received User from Kafka: " + userDto);
        dtoHandler.setUserDto(userDto);
    }
}
