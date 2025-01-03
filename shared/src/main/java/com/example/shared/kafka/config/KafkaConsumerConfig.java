package com.example.shared.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
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

    private final String bootstrapServers;

    @SuppressWarnings("unused")
    public KafkaConsumerConfig() {
        bootstrapServers = "";
    }

    public KafkaConsumerConfig(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }


    public <T> ConsumerFactory<String, T> createConsumerFactory(String groupId, Class<T> valueType) {
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setIdClassMapping(Map.of(valueType.getSimpleName(), valueType));

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(valueType);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setTypeMapper(typeMapper);

        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), jsonDeserializer);
    }

    public <T> ConcurrentKafkaListenerContainerFactory<String, T> createListenerContainerFactory(
            ConsumerFactory<String, T> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
