package com.example.security.service;

import com.example.security.kafka.consumer.KafkaConsumer;
import com.example.security.model.UserImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaAuthenticationService {

    @Autowired
    private KafkaConsumer kafkaConsumer;

    public UserImpl getUserAuthentication(String subject) {
        var dto = kafkaConsumer.requestUserByName(subject);
        var user = new UserImpl(dto);
        return user;
    }
}
