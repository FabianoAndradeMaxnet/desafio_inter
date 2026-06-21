package com.example.todo.infrastructure.realtime;

import com.example.todo.application.event.TaskRealtimeEvent;
import com.example.todo.application.event.TaskRealtimeEventType;
import com.example.todo.domain.TaskStatus;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReactorTaskRealtimeEventNotifierTest {

    @Test
    void deliversRealtimeEventsToSubscribers() throws Exception {
        ReactorTaskRealtimeEventNotifier notifier = new ReactorTaskRealtimeEventNotifier();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<TaskRealtimeEvent> received = new AtomicReference<>();
        TaskRealtimeEvent event = new TaskRealtimeEvent(
                UUID.randomUUID(),
                TaskRealtimeEventType.TASK_CREATED,
                UUID.randomUUID(),
                "Realtime task",
                TaskStatus.TODO,
                null,
                null,
                Instant.now()
        );

        var subscription = Flux.from(notifier.stream())
                .subscribe(next -> {
                    received.set(next);
                    latch.countDown();
                });

        try {
            notifier.notify(event);

            assertTrue(latch.await(2, TimeUnit.SECONDS));
            assertEquals(event, received.get());
        } finally {
            subscription.dispose();
        }
    }
}
