package com.example.todo.infrastructure.messaging;

import com.example.todo.application.event.TaskCreatedEvent;
import com.example.todo.application.event.TaskEventPublisher;
import com.example.todo.application.event.TaskStatusUpdatedEvent;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Singleton
public class KafkaTaskEventPublisher implements TaskEventPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaTaskEventPublisher.class);

    private final TaskKafkaProducer producer;
    private final TaskEventMessageMapper mapper;
    private final ObjectMapper objectMapper;

    public KafkaTaskEventPublisher(TaskKafkaProducer producer,
                                   TaskEventMessageMapper mapper,
                                   ObjectMapper objectMapper) {
        this.producer = producer;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(TaskCreatedEvent event) {
        publish(mapper.toMessage(event));
    }

    @Override
    public void publish(TaskStatusUpdatedEvent event) {
        publish(mapper.toMessage(event));
    }

    private void publish(TaskEventMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            producer.publish(message.taskId().toString(), payload);

            LOG.atInfo()
                    .addKeyValue("eventId", message.eventId())
                    .addKeyValue("eventType", message.type())
                    .addKeyValue("taskId", message.taskId())
                    .log("task.event.published");
        } catch (IOException exception) {
            LOG.atError()
                    .addKeyValue("eventId", message.eventId())
                    .addKeyValue("eventType", message.type())
                    .addKeyValue("taskId", message.taskId())
                    .setCause(exception)
                    .log("task.event.publish_failed");
            throw new TaskEventPublishException(message.eventId().toString(), exception);
        }
    }
}
