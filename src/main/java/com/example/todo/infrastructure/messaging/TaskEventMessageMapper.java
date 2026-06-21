package com.example.todo.infrastructure.messaging;

import com.example.todo.application.event.TaskCreatedEvent;
import com.example.todo.application.event.TaskStatusUpdatedEvent;
import jakarta.inject.Singleton;

@Singleton
public class TaskEventMessageMapper {

    public TaskEventMessage toMessage(TaskCreatedEvent event) {
        return new TaskEventMessage(
                event.eventId(),
                TaskEventType.TASK_CREATED,
                event.taskId(),
                event.title(),
                event.status(),
                null,
                null,
                event.occurredAt()
        );
    }

    public TaskEventMessage toMessage(TaskStatusUpdatedEvent event) {
        return new TaskEventMessage(
                event.eventId(),
                TaskEventType.TASK_STATUS_UPDATED,
                event.taskId(),
                null,
                null,
                event.previousStatus(),
                event.newStatus(),
                event.occurredAt()
        );
    }
}
