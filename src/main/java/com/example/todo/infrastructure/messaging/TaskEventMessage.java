package com.example.todo.infrastructure.messaging;

import com.example.todo.domain.TaskStatus;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

@Serdeable
public record TaskEventMessage(
        UUID eventId,
        TaskEventType type,
        UUID taskId,
        @Nullable String title,
        @Nullable TaskStatus status,
        @Nullable TaskStatus previousStatus,
        @Nullable TaskStatus newStatus,
        Instant occurredAt
) {
}
