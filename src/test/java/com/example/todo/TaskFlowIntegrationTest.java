package com.example.todo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Testcontainers
class TaskFlowIntegrationTest {
    private static final String TASK_EVENTS_TOPIC = "task-events";

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("todo")
            .withUsername("todo")
            .withPassword("todo");

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.7.1")
    ).withKraft();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient;

    @Test
    void executesMainTaskFlowAndPublishesEvents() throws Exception {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, applicationProperties());
             HttpClient client = server.getApplicationContext().createBean(HttpClient.class, server.getURL());
             KafkaConsumer<String, String> eventConsumer = newEventConsumer()) {
            httpClient = client;
            eventConsumer.subscribe(List.of(TASK_EVENTS_TOPIC));

            JsonNode created = createTask("Integration flow", "with Testcontainers");
            String taskId = created.at("/data/id").asText();

            assertNotNull(UUID.fromString(taskId));
            assertEquals("Integration flow", created.at("/data/title").asText());
            assertEquals("TODO", created.at("/data/status").asText());

            JsonNode createdEvent = pollEvent(eventConsumer, "TASK_CREATED");
            assertEquals(taskId, createdEvent.get("taskId").asText());
            assertEquals("Integration flow", createdEvent.get("title").asText());

            JsonNode list = get("/api/v1/tasks?page=0&size=10");
            assertFalse(list.get("data").isEmpty());
            assertTrue(list.at("/meta/totalItems").asLong() >= 1);

            JsonNode statusUpdated = patch(
                    "/api/v1/tasks/" + taskId + "/status",
                    "{\"status\":\"IN_PROGRESS\"}"
            );
            assertEquals("IN_PROGRESS", statusUpdated.at("/data/status").asText());

            JsonNode statusUpdatedEvent = pollEvent(eventConsumer, "TASK_STATUS_UPDATED");
            assertEquals(taskId, statusUpdatedEvent.get("taskId").asText());
            assertEquals("TODO", statusUpdatedEvent.get("previousStatus").asText());
            assertEquals("IN_PROGRESS", statusUpdatedEvent.get("newStatus").asText());

            JsonNode updated = put(
                    "/api/v1/tasks/" + taskId,
                    "{\"title\":\"Integration flow updated\",\"description\":\"updated description\"}"
            );
            assertEquals("Integration flow updated", updated.at("/data/title").asText());

            delete("/api/v1/tasks/" + taskId);
            assertTaskNotFoundOnUpdate(taskId);
        }
    }

    private Map<String, Object> applicationProperties() {
        return Map.of(
                "micronaut.server.port", -1,
                "datasources.default.url", POSTGRES.getJdbcUrl(),
                "datasources.default.username", POSTGRES.getUsername(),
                "datasources.default.password", POSTGRES.getPassword(),
                "kafka.bootstrap.servers", KAFKA.getBootstrapServers(),
                "app.kafka.topics.task-events", TASK_EVENTS_TOPIC,
                "app.kafka.consumer.group-id", "todo-task-events-it-" + UUID.randomUUID(),
                "logger.levels.org.apache.kafka", "WARN"
        );
    }

    private KafkaConsumer<String, String> newEventConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "task-flow-it-" + UUID.randomUUID());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<>(properties);
    }

    private JsonNode createTask(String title, String description) throws Exception {
        String body = """
                {"title":"%s","description":"%s"}
                """.formatted(title, description);
        HttpResponse<String> response = httpClient.toBlocking().exchange(
                HttpRequest.POST("/api/v1/tasks", body).contentType(MediaType.APPLICATION_JSON_TYPE),
                String.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatus());
        return objectMapper.readTree(response.body());
    }

    private JsonNode get(String uri) throws Exception {
        HttpResponse<String> response = httpClient.toBlocking().exchange(HttpRequest.GET(uri), String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        return objectMapper.readTree(response.body());
    }

    private JsonNode put(String uri, String body) throws Exception {
        HttpResponse<String> response = httpClient.toBlocking().exchange(
                HttpRequest.PUT(uri, body).contentType(MediaType.APPLICATION_JSON_TYPE),
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatus());
        return objectMapper.readTree(response.body());
    }

    private JsonNode patch(String uri, String body) throws Exception {
        HttpResponse<String> response = httpClient.toBlocking().exchange(
                HttpRequest.PATCH(uri, body).contentType(MediaType.APPLICATION_JSON_TYPE),
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatus());
        return objectMapper.readTree(response.body());
    }

    private void delete(String uri) {
        HttpResponse<String> response = httpClient.toBlocking().exchange(HttpRequest.DELETE(uri), String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    private void assertTaskNotFoundOnUpdate(String taskId) throws Exception {
        try {
            httpClient.toBlocking().exchange(
                    HttpRequest.PUT(
                            "/api/v1/tasks/" + taskId,
                            "{\"title\":\"Integration flow updated\",\"description\":\"updated description\"}"
                    ).contentType(MediaType.APPLICATION_JSON_TYPE),
                    String.class
            );
            fail("Expected task update to fail with 404 after deletion.");
        } catch (HttpClientResponseException exception) {
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            String responseBody = exception.getResponse().getBody(String.class).orElseThrow();
            assertEquals("TASK_NOT_FOUND", objectMapper.readTree(responseBody).at("/error/code").asText());
        }
    }

    private JsonNode pollEvent(KafkaConsumer<String, String> consumer, String eventType) throws Exception {
        long deadline = System.nanoTime() + Duration.ofSeconds(20).toNanos();
        while (System.nanoTime() < deadline) {
            var records = consumer.poll(Duration.ofMillis(500));
            for (var record : records) {
                JsonNode event = objectMapper.readTree(record.value());
                if (eventType.equals(event.get("type").asText())) {
                    return event;
                }
            }
        }
        throw new AssertionError("Kafka event not received: " + eventType);
    }
}
