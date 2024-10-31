package com.example.users.config;

import com.example.users.repository.UserRepository;
import com.example.users.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository repository;

    @KafkaListener(topics = "FINANCIAL_BANK_TRANSACTIONS", groupId = "user-group")
    public void receiveToken(String token) {
        String userId = tokenService.getSubject(token);  // Replace with actual token parsing logic
        var user = repository.findByName(userId);

        // Send the user ID back to `financial.transactions`
        kafkaTemplate.send("FINANCIAL_BANK_USERS", user.getId());
    }


}
