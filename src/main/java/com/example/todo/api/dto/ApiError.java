package com.example.todo.api.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record ApiError(
        String code,
        String message,
        List<String> details
) {
    public static ApiError of(String code, String message) {
        return new ApiError(code, message, List.of());
    }

    public static ApiError of(String code, String message, List<String> details) {
        return new ApiError(code, message, List.copyOf(details));
    }
}
