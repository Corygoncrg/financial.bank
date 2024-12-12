package com.example.users.kafka;

import com.example.shared.dto.UserValidatorDto;
import com.example.shared.exception.NoUuidFoundException;
import com.example.shared.kafka.responseHandler.KafkaValidatorResponseHandler;
import com.example.shared.model.UserValidator;
import com.example.shared.service.JsonStringWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaUserValidatorService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;


    @Autowired
    private KafkaValidatorResponseHandler validatorResponseHandler;

    public void saveValidator(UserValidator validator) {
        var validatorDto = new UserValidatorDto(validator);
        kafkaTemplate.send("FINANCIAL_BANK_SECURITY_REQUEST_SAVE_VALIDATOR", validatorDto);

    }

    public UserValidator findByUuid(String uuid) {
        try {
            var jsonMessage = new JsonStringWrapper();
            jsonMessage.setValue(uuid);
            kafkaTemplate.send("FINANCIAL_BANK_SECURITY_REQUEST_VALIDATOR", jsonMessage);

        if (!validatorResponseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)) {
            throw new NoUuidFoundException("Timeout waiting for validator from Kafka");
        }

            return new UserValidator(validatorResponseHandler.getUserValidatorDto());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Kafka response", e);
        }
    }

    public void deleteValidator(UserValidator validator) {
        var dto = new UserValidatorDto(validator);
        kafkaTemplate.send("FINANCIAL_BANK_SECURITY_REQUEST_DELETE_VALIDATOR", dto);
    }

    public UserValidator rebuildValidator(UserValidator validator) {
        try {
            var dto = new UserValidatorDto(validator);
            kafkaTemplate.send("FINANCIAL_BANK_SECURITY_REQUEST_REBUILD_VALIDATOR", dto);

            if (!validatorResponseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)) {
                throw new NoUuidFoundException("Timeout waiting for validator from Kafka");
            }
            return new UserValidator(validatorResponseHandler.getUserValidatorDto());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Kafka response", e);
        }
    }

    public UserValidator findByUserId(Long id) {
        try {
            var jsonMessage = new JsonStringWrapper();
            jsonMessage.setValue(String.valueOf(id));
            kafkaTemplate.send("FINANCIAL_BANK_SECURITY_REQUEST_VALIDATOR_KEY", jsonMessage);

            if (!validatorResponseHandler.awaitResponseWithTimeout(10, TimeUnit.SECONDS)) {
                throw new NoUuidFoundException("Timeout waiting for validator from Kafka");
            }

            return new UserValidator(validatorResponseHandler.getUserValidatorDto());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Kafka response", e);
        }
    }
}
