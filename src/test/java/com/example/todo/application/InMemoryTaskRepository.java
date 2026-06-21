package com.example.todo.application;

import com.example.todo.domain.PageResult;
import com.example.todo.domain.Pagination;
import com.example.todo.domain.Task;
import com.example.todo.domain.TaskRepository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

final class InMemoryTaskRepository implements TaskRepository {
    private final Map<UUID, Task> tasks = new LinkedHashMap<>();

    @Override
    public Task save(Task task) {
        tasks.put(task.id(), task);
        return task;
    }

    @Override
    public PageResult<Task> findAll(Pagination pagination) {
        var items = tasks.values().stream()
                .sorted(Comparator.comparing(Task::createdAt))
                .skip((long) pagination.page() * pagination.size())
                .limit(pagination.size())
                .toList();
        int totalPages = (int) Math.ceil((double) tasks.size() / pagination.size());
        return new PageResult<>(items, tasks.size(), pagination.page(), pagination.size(), totalPages);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public void deleteById(UUID id) {
        tasks.remove(id);
    }
}
