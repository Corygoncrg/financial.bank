package com.example.security.kafka;

import com.example.security.repository.UserValidatorRepository;
import com.example.shared.dto.UserValidatorDto;
import com.example.shared.model.User;
import com.example.shared.model.UserValidator;
import com.example.shared.service.JsonStringWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaUserValidatorListenerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private UserValidatorRepository validatorRepository;

    @KafkaListener(topics = "FINANCIAL_BANK_SECURITY_REQUEST_SAVE_VALIDATOR", groupId = "security-group-id", containerFactory = "userDtoKafkaListenerContainerFactory")
    public void saveValidator(UserValidatorDto dto) {
        var validator = new UserValidator(dto);
        validatorRepository.save(validator);
        kafkaTemplate.send("FINANCIAL_BANK_SECURITY_RESPONSE_SAVE_VALIDATOR", "Validator for user " + dto.idUser() + " has been created");
    }
    @KafkaListener(topics = "FINANCIAL_BANK_SECURITY_REQUEST_VALIDATOR", groupId = "security-validator-group-id", containerFactory = "userValidatorKafkaListenerContainerFactory")
    public void findValidatorByUuid(JsonStringWrapper jsonMessage) {
        var validator = validatorRepository.findByUuid(jsonMessage.getValue());
        kafkaTemplate.send("FINANCIAL_BANK_SECURITY_RESPONSE_VALIDATOR", validator);
    }

    @KafkaListener(topics = "FINANCIAL_BANK_SECURITY_REQUEST_DELETE_VALIDATOR", groupId = "security-group-id", containerFactory = "userDtoKafkaListenerContainerFactory")
    public void deleteValidator(UserValidatorDto dto) {

        var validator = new UserValidator(dto);
    }
}
