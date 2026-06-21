package com.example.todo.api.dto;

import com.example.todo.domain.TaskStatus;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;

@Serdeable
public record TaskResponse(
        UUID id,
        String title,
        @Nullable
        String description,
        TaskStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
