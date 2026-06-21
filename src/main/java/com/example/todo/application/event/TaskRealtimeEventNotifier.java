package com.example.todo.application.event;

import org.reactivestreams.Publisher;

public interface TaskRealtimeEventNotifier {
    void notify(TaskRealtimeEvent event);

    Publisher<TaskRealtimeEvent> stream();
}
