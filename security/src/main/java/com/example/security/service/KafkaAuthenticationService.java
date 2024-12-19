package com.example.security.service;

import com.example.security.kafka.consumer.KafkaSecurityConsumer;
import com.example.shared.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaAuthenticationService {

    @Autowired
    private KafkaSecurityConsumer kafkaSecurityConsumer;

    public User getUserAuthentication(String subject) {
        var dto = kafkaSecurityConsumer.requestUserByName(subject);
        return new User(dto);
    }
}
