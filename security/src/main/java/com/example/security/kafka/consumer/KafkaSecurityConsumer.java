package com.example.security.kafka.consumer;

import com.example.security.service.TokenService;
import com.example.shared.dto.UserDto;
import com.example.shared.kafka.responseHandler.KafkaDtoResponseHandler;
import com.example.shared.service.JsonStringWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class KafkaSecurityConsumer {

    @Autowired
    @Qualifier("securityKafkaTemplate")
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KafkaDtoResponseHandler responseHandler;

    @Autowired
    private TokenService tokenService;

    @KafkaListener(topics = "FINANCIAL_BANK_TRANSACTIONS_REQUEST", groupId = "security-string-wrapper-group-id", containerFactory = "jsonStringWrapperKafkaListenerContainerFactory")
    public void receiveToken(JsonStringWrapper json) {
        String userId = tokenService.getSubject(json.getValue());
        var jsonMessage = new JsonStringWrapper();
        jsonMessage.setValue(userId);
        try {
            kafkaTemplate.send("FINANCIAL_BANK_USERS_REQUEST", jsonMessage);

            if (!responseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Timeout waiting for user details from Kafka");
            }

            kafkaTemplate.send("FINANCIAL_BANK_TRANSACTIONS_RESPONSE", responseHandler.getUserDto());
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
