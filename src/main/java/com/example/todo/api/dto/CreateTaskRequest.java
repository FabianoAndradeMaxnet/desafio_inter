package com.example.todo.api.dto;

import com.example.todo.domain.Task;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public record CreateTaskRequest(
        @NotBlank
        @Size(min = Task.MIN_TITLE_LENGTH, max = Task.MAX_TITLE_LENGTH)
        String title,

        @Nullable
        @Size(max = Task.MAX_DESCRIPTION_LENGTH)
        String description
) {
}
