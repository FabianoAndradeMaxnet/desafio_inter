package com.example.todo.infrastructure.messaging;

public class TaskEventPublishException extends RuntimeException {

    public TaskEventPublishException(String eventId, Throwable cause) {
        super("Failed to publish task event: " + eventId, cause);
    }
}
