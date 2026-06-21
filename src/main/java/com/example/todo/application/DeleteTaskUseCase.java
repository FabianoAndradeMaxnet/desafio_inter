package com.example.todo.application;

import com.example.todo.domain.Task;
import com.example.todo.domain.TaskRepository;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Singleton
public class DeleteTaskUseCase {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteTaskUseCase.class);

    private final TaskRepository taskRepository;

    public DeleteTaskUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void execute(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskRepository.deleteById(task.id());

        LOG.atInfo()
                .addKeyValue("taskId", task.id())
                .addKeyValue("status", task.status())
                .log("task.deleted");
    }
}
