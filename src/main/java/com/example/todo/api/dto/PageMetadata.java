package com.example.todo.api.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record PageMetadata(
        long totalItems,
        int page,
        int size,
        int totalPages
) {
}
