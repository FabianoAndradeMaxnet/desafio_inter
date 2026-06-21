package com.example.todo.domain;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    Task save(Task task);

    PageResult<Task> findAll(Pagination pagination);

    Optional<Task> findById(UUID id);

    void deleteById(UUID id);
}
