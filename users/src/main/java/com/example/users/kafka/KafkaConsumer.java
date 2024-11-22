package com.example.users.kafka;

import com.example.users.dto.user.UserDto;
import com.example.users.repository.UserRepository;
import com.example.users.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @Autowired
    private KafkaTemplate<String, UserDto> kafkaTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository repository;

    @KafkaListener(topics = "FINANCIAL_BANK_TRANSACTIONS", groupId = "user-group")
    public void receiveToken(String token) {
        String userId = tokenService.getSubject(token);
        var user = repository.findByName(userId);

        var userDto = new UserDto(user);
        kafkaTemplate.send("FINANCIAL_BANK_USERS", userDto);
    }

}
