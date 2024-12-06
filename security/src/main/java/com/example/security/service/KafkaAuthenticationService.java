package com.example.security.service;

import com.example.security.kafka.consumer.KafkaConsumer;
import com.example.shared.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaAuthenticationService {

    @Autowired
    private KafkaConsumer kafkaConsumer;

    public User getUserAuthentication(String subject) {
        var dto = kafkaConsumer.requestUserByName(subject);
        return new User(dto);
    }
}
