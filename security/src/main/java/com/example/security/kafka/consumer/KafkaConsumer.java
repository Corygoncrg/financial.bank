package com.example.security.kafka.consumer;

import com.example.security.dto.UserAuthenticationDto;
import com.example.security.dto.UserDto;
import com.example.security.kafka.KafkaAuthenticationResponseHandler;
import com.example.security.kafka.KafkaDtoResponseHandler;
import com.example.security.service.JsonStringWrapper;
import com.example.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaConsumer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KafkaDtoResponseHandler responseHandler;

    @Autowired
    private KafkaAuthenticationResponseHandler authenticationHandler;


    @Autowired
    private TokenService tokenService;

//    @KafkaListener(topics = "FINANCIAL_BANK_TRANSACTIONS_REQUEST", groupId = "user-group")
//    public void receiveToken(String token) {
//        String userId = tokenService.getSubject(token);
//        try {
//            kafkaTemplate.send("FINANCIAL_BANK_USERS_REQUEST", userId);
//
//            if (!responseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)) {
//                throw new RuntimeException("Timeout waiting for user details from Kafka");
//            }
//
//            kafkaTemplate.send("FINANCIAL_BANK_TRANSACTIONS_RESPONSE", responseHandler.getUserDto());
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException("Interrupted while waiting for Kafka response", e);
//        }
//    }


    public UserAuthenticationDto getUserAuthentication(String token) {
        String userId = tokenService.getSubject(token);
        try {
            kafkaTemplate.send("FINANCIAL_BANK_USERS_REQUEST_AUTH", token);

            if (!authenticationHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Timeout waiting for user details from Kafka");
            }

            return authenticationHandler.getAuthenticationDto();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Kafka response", e);
        }
    }

    public UserDto requestUserByName(String name) {
        try {
            var jsonMessage = new JsonStringWrapper();
            jsonMessage.setValue(name);
         kafkaTemplate.send("FINANCIAL_BANK_USERS_REQUEST_DTO", jsonMessage);
            if (!responseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Timeout waiting for user details from Kafka");
            }

            return responseHandler.getUserDto();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Kafka response", e);
        }
    }
}
