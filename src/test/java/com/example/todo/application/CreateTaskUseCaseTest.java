package com.example.todo.application;

import com.example.todo.application.dto.CreateTaskCommand;
import com.example.todo.domain.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateTaskUseCaseTest {

    @Test
    void createsTaskAndPublishesTaskCreatedEvent() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        RecordingTaskEventPublisher publisher = new RecordingTaskEventPublisher();
        CreateTaskUseCase useCase = new CreateTaskUseCase(repository, publisher);

        var task = useCase.execute(new CreateTaskCommand("  Publish event  ", "  from unit test  "));

        assertNotNull(task.id());
        assertEquals("Publish event", task.title());
        assertEquals("from unit test", task.description());
        assertEquals(TaskStatus.TODO, task.status());
        assertTrue(repository.findById(task.id()).isPresent());

        assertEquals(1, publisher.createdEvents().size());
        var event = publisher.createdEvents().get(0);
        assertEquals(task.id(), event.taskId());
        assertEquals(task.title(), event.title());
        assertEquals(task.status(), event.status());
    }
}
