package com.example.todo.application.event;

public interface TaskEventPublisher {
    void publish(TaskCreatedEvent event);

    void publish(TaskStatusUpdatedEvent event);
}
