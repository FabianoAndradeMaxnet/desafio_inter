package com.example.todo.api;

import com.example.todo.api.dto.ApiResponse;
import com.example.todo.api.dto.CreateTaskRequest;
import com.example.todo.api.dto.PageResponse;
import com.example.todo.api.dto.TaskResponse;
import com.example.todo.api.dto.UpdateTaskRequest;
import com.example.todo.api.dto.UpdateTaskStatusRequest;
import com.example.todo.api.mapper.TaskApiMapper;
import com.example.todo.application.ChangeTaskStatusUseCase;
import com.example.todo.application.CreateTaskUseCase;
import com.example.todo.application.DeleteTaskUseCase;
import com.example.todo.application.ListTasksUseCase;
import com.example.todo.application.UpdateTaskUseCase;
import com.example.todo.domain.Pagination;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.UUID;

@Controller("/api/v1/tasks")
@Validated
public class TaskController {
    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final ChangeTaskStatusUseCase changeTaskStatusUseCase;
    private final TaskApiMapper mapper;

    public TaskController(CreateTaskUseCase createTaskUseCase,
                          ListTasksUseCase listTasksUseCase,
                          UpdateTaskUseCase updateTaskUseCase,
                          DeleteTaskUseCase deleteTaskUseCase,
                          ChangeTaskStatusUseCase changeTaskStatusUseCase,
                          TaskApiMapper mapper) {
        this.createTaskUseCase = createTaskUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.changeTaskStatusUseCase = changeTaskStatusUseCase;
        this.mapper = mapper;
    }

    @Post
    public HttpResponse<ApiResponse<TaskResponse>> create(@Body @Valid CreateTaskRequest request) {
        TaskResponse response = mapper.toResponse(createTaskUseCase.execute(mapper.toCommand(request)));
        return HttpResponse.created(ApiResponse.success(response));
    }

    @Get
    public PageResponse<TaskResponse> list(
            @QueryValue(defaultValue = "0") @Min(0) int page,
            @QueryValue(defaultValue = "20") @Min(1) @Max(Pagination.MAX_SIZE) int size) {
        return mapper.toPageResponse(listTasksUseCase.execute(Pagination.of(page, size)));
    }

    @Put("/{id}")
    public ApiResponse<TaskResponse> update(@PathVariable UUID id, @Body @Valid UpdateTaskRequest request) {
        return ApiResponse.success(mapper.toResponse(updateTaskUseCase.execute(mapper.toCommand(id, request))));
    }

    @Patch("/{id}/status")
    public ApiResponse<TaskResponse> changeStatus(@PathVariable UUID id, @Body @Valid UpdateTaskStatusRequest request) {
        return ApiResponse.success(mapper.toResponse(changeTaskStatusUseCase.execute(mapper.toCommand(id, request))));
    }

    @Delete("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        deleteTaskUseCase.execute(id);
        return ApiResponse.success(null);
    }
}
