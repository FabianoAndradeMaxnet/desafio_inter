package com.example.todo.application.event;

import com.example.todo.domain.TaskStatus;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

@Serdeable
public record TaskRealtimeEvent(
        UUID eventId,
        TaskRealtimeEventType type,
        @Nullable UUID taskId,
        @Nullable String title,
        @Nullable TaskStatus status,
        @Nullable TaskStatus previousStatus,
        @Nullable TaskStatus newStatus,
        Instant occurredAt
) {
    public static TaskRealtimeEvent connected() {
        return new TaskRealtimeEvent(
                UUID.randomUUID(),
                TaskRealtimeEventType.CONNECTED,
                null,
                null,
                null,
                null,
                null,
                Instant.now()
        );
    }
}
