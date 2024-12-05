package com.example.users.kafka;

import com.example.shared.dto.UserAuthenticationDto;
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

    @KafkaListener(topics = "FINANCIAL_BANK_USERS_REQUEST", groupId = "user-group-id", containerFactory = "userDtoKafkaListenerContainerFactoryUsers")
    public void receiveToken(String userId) {
        var user = repository.findByName(userId);

        var userDto = new UserDto(user);
        kafkaTemplate.send("FINANCIAL_BANK_USERS_RESPONSE", userDto);
    }

    @KafkaListener(topics = "FINANCIAL_BANK_USERS_REQUEST_DTO", groupId = "users-string-wrapper-group-id", containerFactory = "jsonStringWrapperKafkaListenerContainerFactoryUsers")
    public void getUserByUsername(JsonStringWrapper message) {
        var user = repository.findByName(message.getValue());
        if (user == null) return;

        var userDto = new UserDto(user);
        kafkaTemplate.send("FINANCIAL_BANK_USERS_RESPONSE_DTO", userDto);
    }

    @KafkaListener(topics = "FINANCIAL_BANK_USERS_REQUEST_AUTH", groupId = "auth-group-id", containerFactory = "jsonStringWrapperKafkaListenerContainerFactoryUsers")
    public void getUserAuthentication(JsonStringWrapper message) {
        var user = repository.findByName(message.getValue());
        if (user == null) return;

        var authDto = new UserAuthenticationDto(user);
        kafkaTemplate.send("FINANCIAL_BANK_USERS_RESPONSE_AUTH", authDto);
    }

}
