package com.example.todo.application;

import com.example.todo.domain.PageResult;
import com.example.todo.domain.Pagination;
import com.example.todo.domain.Task;
import com.example.todo.domain.TaskRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ListTasksUseCase {
    private static final Logger LOG = LoggerFactory.getLogger(ListTasksUseCase.class);

    private final TaskRepository taskRepository;

    public ListTasksUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public PageResult<Task> execute(Pagination pagination) {
        PageResult<Task> result = taskRepository.findAll(pagination);

        LOG.atInfo()
                .addKeyValue("page", pagination.page())
                .addKeyValue("size", pagination.size())
                .addKeyValue("totalItems", result.totalItems())
                .log("tasks.listed");

        return result;
    }
}
