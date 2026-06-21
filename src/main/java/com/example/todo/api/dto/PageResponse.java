package com.example.todo.api.dto;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record PageResponse<T>(
        List<T> data,
        PageMetadata meta,
        @Nullable
        ApiError error
) {
    public static <T> PageResponse<T> success(List<T> data, PageMetadata meta) {
        return new PageResponse<>(List.copyOf(data), meta, null);
    }
}
