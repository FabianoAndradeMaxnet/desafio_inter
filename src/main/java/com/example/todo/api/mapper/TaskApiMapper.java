package com.example.todo.api.mapper;

import com.example.todo.api.dto.CreateTaskRequest;
import com.example.todo.api.dto.PageResponse;
import com.example.todo.api.dto.PageMetadata;
import com.example.todo.api.dto.TaskResponse;
import com.example.todo.api.dto.UpdateTaskRequest;
import com.example.todo.api.dto.UpdateTaskStatusRequest;
import com.example.todo.application.dto.ChangeTaskStatusCommand;
import com.example.todo.application.dto.CreateTaskCommand;
import com.example.todo.application.dto.UpdateTaskCommand;
import com.example.todo.domain.PageResult;
import com.example.todo.domain.Task;
import jakarta.inject.Singleton;

import java.util.UUID;

@Singleton
public class TaskApiMapper {

    public CreateTaskCommand toCommand(CreateTaskRequest request) {
        return new CreateTaskCommand(request.title(), request.description());
    }

    public ChangeTaskStatusCommand toCommand(UUID id, UpdateTaskStatusRequest request) {
        return new ChangeTaskStatusCommand(id, request.status());
    }

    public UpdateTaskCommand toCommand(UUID id, UpdateTaskRequest request) {
        return new UpdateTaskCommand(id, request.title(), request.description());
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.id(),
                task.title(),
                task.description(),
                task.status(),
                task.createdAt(),
                task.updatedAt()
        );
    }

    public PageResponse<TaskResponse> toPageResponse(PageResult<Task> page) {
        return PageResponse.success(
                page.items().stream().map(this::toResponse).toList(),
                new PageMetadata(page.totalItems(), page.page(), page.size(), page.totalPages())
        );
    }
}
