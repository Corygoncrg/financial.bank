package com.example.users.kafka;

import com.example.shared.dto.UserDto;
import com.example.shared.service.JsonStringWrapper;
import com.example.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private UserRepository repository;

    @KafkaListener(topics = "FINANCIAL_BANK_USERS_REQUEST", groupId = "users-string-wrapper-group-id", containerFactory = "jsonStringWrapperKafkaListenerContainerFactory")
    public void receiveToken(JsonStringWrapper userId) {
        var user = repository.findByName(userId.getValue());

        var userDto = new UserDto(user);
        kafkaTemplate.send("FINANCIAL_BANK_USERS_RESPONSE", userDto);
    }

    @KafkaListener(topics = "FINANCIAL_BANK_USERS_REQUEST_DTO", groupId = "users-string-wrapper-group-id", containerFactory = "jsonStringWrapperKafkaListenerContainerFactory")
    public void getUserByUsername(JsonStringWrapper message) {
        var user = repository.findByName(message.getValue());
        if (user == null) return;

        var userDto = new UserDto(user);
        kafkaTemplate.send("FINANCIAL_BANK_USERS_RESPONSE_DTO", userDto);
    }

}
