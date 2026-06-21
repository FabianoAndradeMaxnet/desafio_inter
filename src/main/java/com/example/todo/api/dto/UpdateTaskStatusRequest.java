package com.example.todo.api.dto;

import com.example.todo.domain.TaskStatus;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record UpdateTaskStatusRequest(@NotNull TaskStatus status) {
}
