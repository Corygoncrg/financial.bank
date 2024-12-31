package com.example.security.kafka;

import com.example.shared.dto.UserDto;
import com.example.shared.kafka.responseHandler.KafkaDtoResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    @Autowired
    private KafkaDtoResponseHandler dtoHandler;


    @KafkaListener(topics = "FINANCIAL_BANK_USERS_RESPONSE", groupId = "security-group-id", containerFactory = "userDtoKafkaListenerContainerFactory")
    public void listen0(UserDto userDto) {
        System.out.println("Received User from Kafka: " + userDto);
        dtoHandler.setUserDto(userDto);
    }

    @KafkaListener(topics = "FINANCIAL_BANK_USERS_RESPONSE_DTO", groupId = "security-group-id", containerFactory = "userDtoKafkaListenerContainerFactory")
    public void listen(UserDto userDto) {
        System.out.println("Received User from Kafka: " + userDto);
        dtoHandler.setUserDto(userDto);
    }

}
