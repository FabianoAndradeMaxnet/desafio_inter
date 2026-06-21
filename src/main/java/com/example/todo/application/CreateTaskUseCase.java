package com.example.todo.application;

import com.example.todo.application.dto.CreateTaskCommand;
import com.example.todo.application.event.TaskCreatedEvent;
import com.example.todo.application.event.TaskEventPublisher;
import com.example.todo.domain.Task;
import com.example.todo.domain.TaskRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CreateTaskUseCase {
    private static final Logger LOG = LoggerFactory.getLogger(CreateTaskUseCase.class);

    private final TaskRepository taskRepository;
    private final TaskEventPublisher taskEventPublisher;

    public CreateTaskUseCase(TaskRepository taskRepository, TaskEventPublisher taskEventPublisher) {
        this.taskRepository = taskRepository;
        this.taskEventPublisher = taskEventPublisher;
    }

    public Task execute(CreateTaskCommand command) {
        Task task = Task.create(command.title(), command.description());
        Task saved = taskRepository.save(task);

        LOG.atInfo()
                .addKeyValue("taskId", saved.id())
                .addKeyValue("status", saved.status())
                .log("task.created");

        taskEventPublisher.publish(TaskCreatedEvent.of(saved.id(), saved.title(), saved.status()));

        return saved;
    }
}
