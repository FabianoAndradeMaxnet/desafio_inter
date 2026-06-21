package com.example.todo.application;

import com.example.todo.application.event.TaskCreatedEvent;
import com.example.todo.application.event.TaskEventPublisher;
import com.example.todo.application.event.TaskStatusUpdatedEvent;

import java.util.ArrayList;
import java.util.List;

final class RecordingTaskEventPublisher implements TaskEventPublisher {
    private final List<TaskCreatedEvent> createdEvents = new ArrayList<>();
    private final List<TaskStatusUpdatedEvent> statusUpdatedEvents = new ArrayList<>();

    @Override
    public void publish(TaskCreatedEvent event) {
        createdEvents.add(event);
    }

    @Override
    public void publish(TaskStatusUpdatedEvent event) {
        statusUpdatedEvents.add(event);
    }

    List<TaskCreatedEvent> createdEvents() {
        return createdEvents;
    }

    List<TaskStatusUpdatedEvent> statusUpdatedEvents() {
        return statusUpdatedEvents;
    }
}
