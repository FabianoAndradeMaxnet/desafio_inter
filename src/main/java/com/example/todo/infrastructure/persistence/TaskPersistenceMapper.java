package com.example.todo.infrastructure.persistence;

import com.example.todo.domain.Task;
import jakarta.inject.Singleton;

@Singleton
public class TaskPersistenceMapper {

    public TaskEntity toEntity(Task task) {
        return new TaskEntity(
                task.id(),
                task.title(),
                task.description(),
                task.status(),
                task.createdAt(),
                task.updatedAt()
        );
    }

    public Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
