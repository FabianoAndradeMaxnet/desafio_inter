package com.example.todo.application.event;

import com.example.todo.domain.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public record TaskStatusUpdatedEvent(
        UUID eventId,
        UUID taskId,
        TaskStatus previousStatus,
        TaskStatus newStatus,
        Instant occurredAt
) {
    public static TaskStatusUpdatedEvent of(UUID taskId, TaskStatus previousStatus, TaskStatus newStatus) {
        return new TaskStatusUpdatedEvent(UUID.randomUUID(), taskId, previousStatus, newStatus, Instant.now());
    }
}
