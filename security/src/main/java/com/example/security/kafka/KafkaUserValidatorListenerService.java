package com.example.security.kafka;

import com.example.security.repository.UserValidatorRepository;
import com.example.shared.dto.UserValidatorDto;
import com.example.shared.model.UserValidator;
import com.example.shared.service.JsonStringWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaUserValidatorListenerService {

    @Autowired
    @Qualifier("securityKafkaTemplate")
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private UserValidatorRepository validatorRepository;

    @KafkaListener(topics = "FINANCIAL_BANK_SECURITY_REQUEST_SAVE_VALIDATOR", groupId = "security-validator-group-id", containerFactory = "userValidatorDtoKafkaListenerContainerFactory")
    public void saveValidator(UserValidatorDto dto) {
        var validator = new UserValidator(dto);
        validatorRepository.save(validator);
        kafkaTemplate.send("FINANCIAL_BANK_SECURITY_RESPONSE_SAVE_VALIDATOR", new UserValidatorDto(validator));
    }
    @KafkaListener(topics = "FINANCIAL_BANK_SECURITY_REQUEST_VALIDATOR", groupId = "security-string-wrapper-group-id", containerFactory = "jsonStringWrapperKafkaListenerContainerFactory")
    public void findValidatorByUuid(JsonStringWrapper jsonMessage) {
        var validatorOptional = validatorRepository.findByUuid(jsonMessage.getValue());
        if (validatorOptional.isPresent()) {
        var dto = new UserValidatorDto(validatorOptional.get());
        kafkaTemplate.send("FINANCIAL_BANK_SECURITY_RESPONSE_VALIDATOR", dto);
        }
    }

    @KafkaListener(topics = "FINANCIAL_BANK_SECURITY_REQUEST_VALIDATOR_KEY", groupId = "security-string-wrapper-group-id", containerFactory = "jsonStringWrapperKafkaListenerContainerFactory")
    public void findValidatorByUserId(JsonStringWrapper jsonMessage) {
        var validatorOptional = validatorRepository.findByIdUser_Id(Long.valueOf(jsonMessage.getValue()));
        if (validatorOptional.isPresent()) {
        var dto = new UserValidatorDto(validatorOptional.get());
        kafkaTemplate.send("FINANCIAL_BANK_SECURITY_RESPONSE_VALIDATOR_KEY", dto);
        }
    }

    @KafkaListener(topics = "FINANCIAL_BANK_SECURITY_REQUEST_DELETE_VALIDATOR", groupId = "security-validator-group-id", containerFactory = "userValidatorDtoKafkaListenerContainerFactory")
    public void deleteValidator(UserValidatorDto dto) {
        var validator = new UserValidator(dto);
        validatorRepository.delete(validator);
    }
    @KafkaListener(topics = "FINANCIAL_BANK_SECURITY_REQUEST_REBUILD_VALIDATOR", groupId = "security-validator-group-id", containerFactory = "userValidatorDtoKafkaListenerContainerFactory")
    public void rebuildValidator(UserValidatorDto dto) {
        validatorRepository.delete(new UserValidator(dto));
        var validator = new UserValidator(dto);
        validator.rebuild();
        validatorRepository.save(validator);
        var validatorDto = new UserValidatorDto(validator);
        kafkaTemplate.send("FINANCIAL_BANK_SECURITY_RESPONSE_REBUILD_VALIDATOR", validatorDto);
    }
}
