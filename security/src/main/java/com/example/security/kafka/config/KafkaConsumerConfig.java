package com.example.security.kafka.config;

import com.example.shared.dto.UserAuthenticationDto;
import com.example.shared.dto.UserDto;
import com.example.shared.service.JsonStringWrapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    @Bean
    public ConsumerFactory<String, UserDto> userDtoConsumerFactory() {
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setIdClassMapping(Map.of("UserDto", UserDto.class));
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "security-group-id");

        JsonDeserializer<UserDto> jsonDeserializer = new JsonDeserializer<>(UserDto.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setTypeMapper(typeMapper);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), jsonDeserializer);
    }
    @Bean
    public ConsumerFactory<String, UserAuthenticationDto> authDtoConsumerFactory() {
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setIdClassMapping(Map.of("UserAuthenticationDto", UserAuthenticationDto.class));
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "security-auth-group-id");

        JsonDeserializer<UserAuthenticationDto> jsonDeserializer = new JsonDeserializer<>(UserAuthenticationDto.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setTypeMapper(typeMapper);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConsumerFactory<String, JsonStringWrapper> jsonStringWrapperConsumerFactory() {
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setIdClassMapping(Map.of("JsonStringWrapper", JsonStringWrapper.class));
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "security-string-wrapper-group-id");

        JsonDeserializer<JsonStringWrapper> jsonDeserializer = new JsonDeserializer<>(JsonStringWrapper.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setTypeMapper(typeMapper);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDto> userDtoKafkaListenerContainerFactorySecurity() {
        ConcurrentKafkaListenerContainerFactory<String, UserDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userDtoConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserAuthenticationDto> AuthDtoKafkaListenerContainerFactorySecurity() {
        ConcurrentKafkaListenerContainerFactory<String, UserAuthenticationDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(authDtoConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JsonStringWrapper> jsonStringWrapperKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, JsonStringWrapper> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(jsonStringWrapperConsumerFactory());
        return factory;
    }
}
