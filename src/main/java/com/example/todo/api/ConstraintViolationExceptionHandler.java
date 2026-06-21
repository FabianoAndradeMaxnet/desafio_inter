package com.example.todo.api;

import com.example.todo.api.dto.ApiError;
import com.example.todo.api.dto.ApiResponse;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.stream.Collectors;

@Produces
@Singleton
@Replaces(ConstraintExceptionHandler.class)
@Requires(classes = {ConstraintViolationException.class, ExceptionHandler.class})
public class ConstraintViolationExceptionHandler
        implements ExceptionHandler<ConstraintViolationException, HttpResponse<ApiResponse<Void>>> {

    @Override
    public HttpResponse<ApiResponse<Void>> handle(HttpRequest request, ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .sorted()
                .toList();
        String message = details.stream().collect(Collectors.joining("; "));

        return HttpResponse.badRequest(ApiResponse.failure(
                ApiError.of("VALIDATION_ERROR", message, details)
        ));
    }
}
