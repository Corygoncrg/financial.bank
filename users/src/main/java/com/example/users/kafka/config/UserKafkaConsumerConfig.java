package com.example.users.kafka.config;

import com.example.shared.dto.UserDto;
import com.example.shared.dto.UserValidatorDto;
import com.example.shared.kafka.config.KafkaConsumerConfig;
import com.example.shared.service.JsonStringWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class UserKafkaConsumerConfig extends KafkaConsumerConfig {

    public UserKafkaConsumerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        super(bootstrapServers);
    }
    @Bean
    public ConsumerFactory<String, UserDto> userDtoConsumerFactory() {
        return createConsumerFactory("user-group-id", UserDto.class);
    }

    @Bean
    public ConsumerFactory<String, JsonStringWrapper> jsonStringWrapperConsumerFactory() {
        return createConsumerFactory("user-string-wrapper-group-id", JsonStringWrapper.class);
    }
    @Bean
    public ConsumerFactory<String, UserValidatorDto> userValidatorDtoConsumerFactory() {
        return createConsumerFactory("user-validator-group-id", UserValidatorDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDto> userDtoKafkaListenerContainerFactory() {
        return createListenerContainerFactory(userDtoConsumerFactory());
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JsonStringWrapper> jsonStringWrapperKafkaListenerContainerFactory() {
        return createListenerContainerFactory(jsonStringWrapperConsumerFactory());
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserValidatorDto> userValidatorDtoKafkaListenerContainerFactory() {
        return createListenerContainerFactory(userValidatorDtoConsumerFactory());
    }
}
