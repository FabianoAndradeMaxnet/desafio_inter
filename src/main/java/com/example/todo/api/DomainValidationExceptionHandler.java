package com.example.todo.api;

import com.example.todo.api.dto.ApiError;
import com.example.todo.api.dto.ApiResponse;
import com.example.todo.domain.DomainValidationException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {DomainValidationException.class, ExceptionHandler.class})
public class DomainValidationExceptionHandler
        implements ExceptionHandler<DomainValidationException, HttpResponse<ApiResponse<Void>>> {

    @Override
    public HttpResponse<ApiResponse<Void>> handle(HttpRequest request, DomainValidationException exception) {
        return HttpResponse.badRequest(ApiResponse.failure(
                ApiError.of("DOMAIN_VALIDATION_ERROR", exception.getMessage())
        ));
    }
}
