package com.example.todo.application.dto;

import java.util.UUID;

public record UpdateTaskCommand(UUID id, String title, String description) {
}
