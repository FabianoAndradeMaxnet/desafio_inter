package com.example.todo.application.dto;

import com.example.todo.domain.TaskStatus;

import java.util.UUID;

public record ChangeTaskStatusCommand(UUID id, TaskStatus status) {
}
