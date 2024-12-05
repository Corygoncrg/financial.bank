package com.example.security.kafka.config;

import com.example.shared.dto.UserAuthenticationDto;
import com.example.shared.dto.UserDto;
import com.example.shared.kafka.config.KafkaConsumerConfig;
import com.example.shared.service.JsonStringWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class SecurityKafkaConsumerConfig extends KafkaConsumerConfig {

    public SecurityKafkaConsumerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        super(bootstrapServers);
    }


    @Bean
    public ConsumerFactory<String, UserDto> userDtoConsumerFactory() {
        return createConsumerFactory("security-group-id", UserDto.class);
    }

    @Bean
    public ConsumerFactory<String, UserAuthenticationDto> authDtoConsumerFactory() {
        return createConsumerFactory("security-auth-group-id", UserAuthenticationDto.class);
    }

    @Bean
    public ConsumerFactory<String, JsonStringWrapper> jsonStringWrapperConsumerFactory() {
        return createConsumerFactory("security-string-wrapper-group-id", JsonStringWrapper.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDto> userDtoKafkaListenerContainerFactory() {
        return createListenerContainerFactory(userDtoConsumerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserAuthenticationDto> authDtoKafkaListenerContainerFactory() {
        return createListenerContainerFactory(authDtoConsumerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JsonStringWrapper> jsonStringWrapperKafkaListenerContainerFactory() {
        return createListenerContainerFactory(jsonStringWrapperConsumerFactory());
    }
}
