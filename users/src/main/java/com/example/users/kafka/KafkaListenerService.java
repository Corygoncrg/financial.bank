package com.example.users.kafka;

import com.example.shared.dto.UserValidatorDto;
import com.example.shared.kafka.responseHandler.KafkaValidatorResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    @Autowired
    private KafkaValidatorResponseHandler validatorResponseHandler;

    @KafkaListener(topics = "FINANCIAL_BANK_SECURITY_RESPONSE_SAVE_VALIDATOR", groupId = "user-validator-group-id", containerFactory = "userValidatorDtoKafkaListenerContainerFactory")
    public void listen(UserValidatorDto validatorDto) {
        System.out.println("Validator successfully created from kafka: " + validatorDto.id());
        validatorResponseHandler.setValidatorDto(validatorDto);
    }

    @KafkaListener(topics = "FINANCIAL_BANK_SECURITY_RESPONSE_VALIDATOR", groupId = "user-validator-group-id", containerFactory = "userValidatorDtoKafkaListenerContainerFactory")
    public void listen2(UserValidatorDto validatorDto) {
        System.out.println("Received validator from kafka: " + validatorDto.id());
        validatorResponseHandler.setValidatorDto(validatorDto);
    }
}
