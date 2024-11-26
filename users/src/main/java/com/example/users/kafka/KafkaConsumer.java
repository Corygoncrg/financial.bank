package com.example.users.kafka;

import com.example.users.dto.user.UserAuthenticationDto;
import com.example.users.dto.user.UserDto;
import com.example.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @Autowired
    private KafkaTemplate<String, UserDto> userDtoTemplate;

    @Autowired
    private KafkaTemplate<String, UserAuthenticationDto> userDetailsTemplate;


    @Autowired
    private UserRepository repository;

    @KafkaListener(topics = "FINANCIAL_BANK_USERS_REQUEST", groupId = "user-group")
    public void receiveToken(String userId) {
        var user = repository.findByName(userId);

        var userDto = new UserDto(user);
        userDtoTemplate.send("FINANCIAL_BANK_USERS_RESPONSE", userDto);
    }

    @KafkaListener(topics = "FINANCIAL_BANK_USERS_REQUEST", groupId = "user-group")
    public void getUserByUsername(UserAuthenticationDto dto) {
        var user = repository.findByName(dto.username());
        if (user == null) return;

        var userDto = new UserDto(user);
        userDtoTemplate.send("FINANCIAL_BANK_USERS_RESPONSE", userDto);
    }

}
