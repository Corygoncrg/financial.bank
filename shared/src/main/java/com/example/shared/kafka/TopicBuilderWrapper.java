package com.example.shared.kafka;

import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;

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
    public NewTopic buildControl(String topic, int partitions, int replicas) {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
