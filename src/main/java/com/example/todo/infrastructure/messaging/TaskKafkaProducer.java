package com.example.todo.infrastructure.messaging;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface TaskKafkaProducer {

    @Topic("${app.kafka.topics.task-events}")
    void publish(@KafkaKey String key, String payload);
}
