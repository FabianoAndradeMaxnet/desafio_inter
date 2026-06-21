package com.example.todo.application;

import com.example.todo.application.dto.UpdateTaskCommand;
import com.example.todo.domain.Task;
import com.example.todo.domain.TaskRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UpdateTaskUseCase {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateTaskUseCase.class);

    private final TaskRepository taskRepository;

    public UpdateTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(UpdateTaskCommand command) {
        Task task = taskRepository.findById(command.id())
                .orElseThrow(() -> new TaskNotFoundException(command.id()));

        Task saved = taskRepository.save(task.updateDetails(command.title(), command.description()));

        LOG.atInfo()
                .addKeyValue("taskId", saved.id())
                .addKeyValue("status", saved.status())
                .log("task.updated");

        return saved;
    }
}
