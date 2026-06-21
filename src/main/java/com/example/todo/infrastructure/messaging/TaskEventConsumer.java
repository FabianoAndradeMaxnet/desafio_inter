package com.example.todo.infrastructure.messaging;

import com.example.todo.application.event.TaskRealtimeEvent;
import com.example.todo.application.event.TaskRealtimeEventNotifier;
import com.example.todo.application.event.TaskRealtimeEventType;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.serde.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@KafkaListener(
        groupId = "${app.kafka.consumer.group-id}",
        offsetReset = OffsetReset.EARLIEST
)
public class TaskEventConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(TaskEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final TaskRealtimeEventNotifier realtimeEventNotifier;

    public TaskEventConsumer(ObjectMapper objectMapper, TaskRealtimeEventNotifier realtimeEventNotifier) {
        this.objectMapper = objectMapper;
        this.realtimeEventNotifier = realtimeEventNotifier;
    }

    @Topic("${app.kafka.topics.task-events}")
    public void consume(@KafkaKey String key, String payload) throws IOException {
        TaskEventMessage message = objectMapper.readValue(payload, TaskEventMessage.class);

        LOG.atInfo()
                .addKeyValue("eventId", message.eventId())
                .addKeyValue("eventType", message.type())
                .addKeyValue("taskId", message.taskId())
                .addKeyValue("messageKey", key)
                .log("task.event.consumed");

        handle(message);
    }

    private void handle(TaskEventMessage message) {
        switch (message.type()) {
            case TASK_CREATED -> {
                LOG.atInfo()
                        .addKeyValue("eventId", message.eventId())
                        .addKeyValue("taskId", message.taskId())
                        .addKeyValue("status", message.status())
                        .log("task.created.event.processed");
                realtimeEventNotifier.notify(toRealtimeEvent(message));
            }
            case TASK_STATUS_UPDATED -> {
                LOG.atInfo()
                        .addKeyValue("eventId", message.eventId())
                        .addKeyValue("taskId", message.taskId())
                        .addKeyValue("previousStatus", message.previousStatus())
                        .addKeyValue("newStatus", message.newStatus())
                        .log("task.status_updated.event.processed");
                realtimeEventNotifier.notify(toRealtimeEvent(message));
            }
        }
    }

    private TaskRealtimeEvent toRealtimeEvent(TaskEventMessage message) {
        TaskRealtimeEventType type = switch (message.type()) {
            case TASK_CREATED -> TaskRealtimeEventType.TASK_CREATED;
            case TASK_STATUS_UPDATED -> TaskRealtimeEventType.TASK_STATUS_UPDATED;
        };

        return new TaskRealtimeEvent(
                message.eventId(),
                type,
                message.taskId(),
                message.title(),
                message.status(),
                message.previousStatus(),
                message.newStatus(),
                message.occurredAt()
        );
    }
}
