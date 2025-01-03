package com.example.shared.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

public class TopicBuilderWrapper {

    private final int partitions;
    private final int replicas;

    public TopicBuilderWrapper(int partitions, int replicas) {
        this.partitions = partitions;
        this.replicas = replicas;
    }

    public NewTopic build(String topic) {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @SuppressWarnings("unused")
    public NewTopic buildControl(String topic, int partitions, int replicas) {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
