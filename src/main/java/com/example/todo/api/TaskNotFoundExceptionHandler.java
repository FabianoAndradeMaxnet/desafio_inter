package com.example.todo.api;

import com.example.todo.api.dto.ApiError;
import com.example.todo.api.dto.ApiResponse;
import com.example.todo.application.TaskNotFoundException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {TaskNotFoundException.class, ExceptionHandler.class})
public class TaskNotFoundExceptionHandler
        implements ExceptionHandler<TaskNotFoundException, HttpResponse<ApiResponse<Void>>> {

    @Override
    public HttpResponse<ApiResponse<Void>> handle(HttpRequest request, TaskNotFoundException exception) {
        return HttpResponse.notFound(ApiResponse.failure(
                ApiError.of("TASK_NOT_FOUND", exception.getMessage())
        ));
    }
}
