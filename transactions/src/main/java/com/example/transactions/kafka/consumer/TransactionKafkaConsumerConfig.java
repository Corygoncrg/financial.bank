package com.example.transactions.kafka.consumer;

import com.example.shared.dto.UserDto;
import com.example.shared.kafka.config.KafkaConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class TransactionKafkaConsumerConfig extends KafkaConsumerConfig {

    public TransactionKafkaConsumerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        super(bootstrapServers);
    }

    @Bean
    public ConsumerFactory<String, UserDto> userDtoConsumerFactory() {
        return createConsumerFactory("transaction-group-id", UserDto.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDto> userDtoKafkaListenerContainerFactory() {
        return createListenerContainerFactory(userDtoConsumerFactory());
    }

}
