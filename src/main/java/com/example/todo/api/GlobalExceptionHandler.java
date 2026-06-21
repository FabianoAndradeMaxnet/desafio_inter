package com.example.todo.api;

import com.example.todo.api.dto.ApiError;
import com.example.todo.api.dto.ApiResponse;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces
@Singleton
@Requires(classes = {Throwable.class, ExceptionHandler.class})
public class GlobalExceptionHandler implements ExceptionHandler<Throwable, HttpResponse<ApiResponse<Void>>> {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public HttpResponse<ApiResponse<Void>> handle(HttpRequest request, Throwable exception) {
        LOG.atError()
                .addKeyValue("path", request.getPath())
                .addKeyValue("method", request.getMethodName())
                .setCause(exception)
                .log("api.unhandled_error");

        return HttpResponse.serverError(ApiResponse.failure(
                ApiError.of("INTERNAL_SERVER_ERROR", "Unexpected internal error")
        ));
    }
}
