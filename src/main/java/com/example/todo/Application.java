package com.example.todo;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Todo Senior Challenge API",
                version = "0.1.0",
                description = "Task management API built with Micronaut, PostgreSQL, JPA and Flyway.",
                contact = @Contact(name = "Todo API Team"),
                license = @License(name = "Apache 2.0")
        ),
        servers = {
                @Server(url = "/api/v1", description = "Version 1")
        }
)
public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
