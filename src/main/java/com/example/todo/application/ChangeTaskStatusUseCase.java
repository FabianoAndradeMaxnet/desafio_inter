package com.example.todo.application;

import com.example.todo.application.dto.ChangeTaskStatusCommand;
import com.example.todo.application.event.TaskEventPublisher;
import com.example.todo.application.event.TaskStatusUpdatedEvent;
import com.example.todo.domain.Task;
import com.example.todo.domain.TaskRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ChangeTaskStatusUseCase {
    private static final Logger LOG = LoggerFactory.getLogger(ChangeTaskStatusUseCase.class);

    private final TaskRepository taskRepository;
    private final TaskEventPublisher taskEventPublisher;

    public ChangeTaskStatusUseCase(TaskRepository taskRepository, TaskEventPublisher taskEventPublisher) {
        this.taskRepository = taskRepository;
        this.taskEventPublisher = taskEventPublisher;
    }

    public Task execute(ChangeTaskStatusCommand command) {
        Task task = taskRepository.findById(command.id())
                .orElseThrow(() -> new TaskNotFoundException(command.id()));

        Task saved = taskRepository.save(task.changeStatus(command.status()));

        LOG.atInfo()
                .addKeyValue("taskId", saved.id())
                .addKeyValue("previousStatus", task.status())
                .addKeyValue("status", saved.status())
                .log("task.status_changed");

        if (task.status() != saved.status()) {
            taskEventPublisher.publish(TaskStatusUpdatedEvent.of(saved.id(), task.status(), saved.status()));
        }

        return saved;
    }
}
