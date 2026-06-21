package com.example.todo.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    @Test
    void createsTaskWithNormalizedValues() {
        Task task = Task.create("  Write tests  ", "  important coverage  ");

        assertEquals("Write tests", task.title());
        assertEquals("important coverage", task.description());
        assertEquals(TaskStatus.TODO, task.status());
        assertTrue(!task.updatedAt().isBefore(task.createdAt()));
    }

    @Test
    void convertsBlankDescriptionToNull() {
        Task task = Task.create("Write tests", "   ");

        assertNull(task.description());
    }

    @Test
    void rejectsInvalidTitle() {
        assertThrows(DomainValidationException.class, () -> Task.create("ab", null));
    }

    @Test
    void updatesTaskDetailsWithNormalizedValues() {
        Task task = Task.create("Write tests", "important coverage");

        Task updated = task.updateDetails("  Review tests  ", "  keep them focused  ");

        assertEquals(task.id(), updated.id());
        assertEquals("Review tests", updated.title());
        assertEquals("keep them focused", updated.description());
        assertEquals(task.status(), updated.status());
        assertEquals(task.createdAt(), updated.createdAt());
        assertTrue(updated.updatedAt().isAfter(task.updatedAt()) || updated.updatedAt().equals(task.updatedAt()));
    }

    @Test
    void rejectsInvalidTitleOnUpdate() {
        Task task = Task.create("Write tests", null);

        assertThrows(DomainValidationException.class, () -> task.updateDetails("ab", null));
    }

    @Test
    void rejectsStatusChangeFromTerminalStatus() {
        Instant now = Instant.now();
        Task done = new Task(UUID.randomUUID(), "Write tests", null, TaskStatus.DONE, now, now);

        assertThrows(DomainValidationException.class, () -> done.changeStatus(TaskStatus.IN_PROGRESS));
    }
}
