package com.example.todo.application.event;

import com.example.todo.domain.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public record TaskCreatedEvent(
        UUID eventId,
        UUID taskId,
        String title,
        TaskStatus status,
        Instant occurredAt
) {
    public static TaskCreatedEvent of(UUID taskId, String title, TaskStatus status) {
        return new TaskCreatedEvent(UUID.randomUUID(), taskId, title, status, Instant.now());
    }
}
