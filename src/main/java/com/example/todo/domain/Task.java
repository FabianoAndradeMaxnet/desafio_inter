package com.example.todo.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Task {
    public static final int MIN_TITLE_LENGTH = 3;
    public static final int MAX_TITLE_LENGTH = 120;
    public static final int MAX_DESCRIPTION_LENGTH = 500;

    private final UUID id;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Task(UUID id,
                String title,
                String description,
                TaskStatus status,
                Instant createdAt,
                Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = validateTitle(title);
        this.description = validateDescription(description);
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        validateTimestamps(this.createdAt, this.updatedAt);
    }

    public static Task create(String title, String description) {
        Instant now = Instant.now();
        return new Task(UUID.randomUUID(), title, description, TaskStatus.TODO, now, now);
    }

    public Task changeStatus(TaskStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus");
        if (status == newStatus) {
            return this;
        }
        if (status.isTerminal()) {
            throw new DomainValidationException("Terminal tasks cannot change status");
        }
        return new Task(id, title, description, newStatus, createdAt, Instant.now());
    }

    public Task updateDetails(String newTitle, String newDescription) {
        String normalizedTitle = validateTitle(newTitle);
        String normalizedDescription = validateDescription(newDescription);

        if (title.equals(normalizedTitle) && Objects.equals(description, normalizedDescription)) {
            return this;
        }

        return new Task(id, normalizedTitle, normalizedDescription, status, createdAt, Instant.now());
    }

    public UUID id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public TaskStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    private static String validateTitle(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("Task title is required");
        }

        String normalized = value.trim();
        if (normalized.length() < MIN_TITLE_LENGTH || normalized.length() > MAX_TITLE_LENGTH) {
            throw new DomainValidationException("Task title must be between 3 and 120 characters");
        }
        return normalized;
    }

    private static String validateDescription(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();
        if (normalized.length() > MAX_DESCRIPTION_LENGTH) {
            throw new DomainValidationException("Task description must be at most 500 characters");
        }
        return normalized;
    }

    private static void validateTimestamps(Instant createdAt, Instant updatedAt) {
        if (updatedAt.isBefore(createdAt)) {
            throw new DomainValidationException("Task updatedAt cannot be before createdAt");
        }
    }
}
