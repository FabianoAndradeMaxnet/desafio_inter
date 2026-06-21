package com.example.todo.api;

import com.example.todo.application.event.TaskRealtimeEvent;
import com.example.todo.application.event.TaskRealtimeEventNotifier;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.sse.Event;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

@Controller("/api/v1/tasks/events")
public class TaskEventStreamController {
    private final TaskRealtimeEventNotifier notifier;

    public TaskEventStreamController(TaskRealtimeEventNotifier notifier) {
        this.notifier = notifier;
    }

    @Get
    @Produces(MediaType.TEXT_EVENT_STREAM)
    public Publisher<Event<TaskRealtimeEvent>> stream() {
        return Flux.concat(
                Flux.just(TaskRealtimeEvent.connected()),
                Flux.from(notifier.stream())
        ).map(event -> Event.of(event)
                .id(event.eventId().toString())
                .name(event.type().name()));
    }
}
