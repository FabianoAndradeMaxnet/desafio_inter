package com.example.todo.application;

import com.example.todo.application.dto.ChangeTaskStatusCommand;
import com.example.todo.domain.Task;
import com.example.todo.domain.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChangeTaskStatusUseCaseTest {

    @Test
    void changesStatusAndPublishesStatusUpdatedEvent() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        RecordingTaskEventPublisher publisher = new RecordingTaskEventPublisher();
        Task task = repository.save(Task.create("Change status", null));
        ChangeTaskStatusUseCase useCase = new ChangeTaskStatusUseCase(repository, publisher);

        var updated = useCase.execute(new ChangeTaskStatusCommand(task.id(), TaskStatus.IN_PROGRESS));

        assertEquals(TaskStatus.IN_PROGRESS, updated.status());
        assertEquals(TaskStatus.IN_PROGRESS, repository.findById(task.id()).orElseThrow().status());

        assertEquals(1, publisher.statusUpdatedEvents().size());
        var event = publisher.statusUpdatedEvents().get(0);
        assertEquals(task.id(), event.taskId());
        assertEquals(TaskStatus.TODO, event.previousStatus());
        assertEquals(TaskStatus.IN_PROGRESS, event.newStatus());
    }

    @Test
    void doesNotPublishStatusEventWhenStatusDoesNotChange() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        RecordingTaskEventPublisher publisher = new RecordingTaskEventPublisher();
        Task task = repository.save(Task.create("Same status", null));
        ChangeTaskStatusUseCase useCase = new ChangeTaskStatusUseCase(repository, publisher);

        var updated = useCase.execute(new ChangeTaskStatusCommand(task.id(), TaskStatus.TODO));

        assertEquals(TaskStatus.TODO, updated.status());
        assertTrue(publisher.statusUpdatedEvents().isEmpty());
    }
}
