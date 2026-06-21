package com.example.todo.infrastructure.realtime;

import com.example.todo.application.event.TaskRealtimeEvent;
import com.example.todo.application.event.TaskRealtimeEventNotifier;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;

@Singleton
public class ReactorTaskRealtimeEventNotifier implements TaskRealtimeEventNotifier {
    private static final Logger LOG = LoggerFactory.getLogger(ReactorTaskRealtimeEventNotifier.class);

    private final Sinks.Many<TaskRealtimeEvent> sink = Sinks.many().multicast().directBestEffort();

    @Override
    public void notify(TaskRealtimeEvent event) {
        Sinks.EmitResult result = sink.tryEmitNext(event);
        if (result.isFailure() && result != Sinks.EmitResult.FAIL_ZERO_SUBSCRIBER) {
            LOG.atWarn()
                    .addKeyValue("eventId", event.eventId())
                    .addKeyValue("eventType", event.type())
                    .addKeyValue("result", result)
                    .log("task.realtime_event.dropped");
        }
    }

    @Override
    public Publisher<TaskRealtimeEvent> stream() {
        return sink.asFlux();
    }
}
