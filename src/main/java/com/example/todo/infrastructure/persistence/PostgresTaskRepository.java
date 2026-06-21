package com.example.todo.infrastructure.persistence;

import com.example.todo.domain.PageResult;
import com.example.todo.domain.Pagination;
import com.example.todo.domain.Task;
import com.example.todo.domain.TaskRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class PostgresTaskRepository implements TaskRepository {
    private final MicronautTaskJpaRepository repository;
    private final TaskPersistenceMapper mapper;

    public PostgresTaskRepository(MicronautTaskJpaRepository repository, TaskPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Task save(Task task) {
        TaskEntity entity = mapper.toEntity(task);
        TaskEntity persisted = repository.existsById(task.id())
                ? repository.update(entity)
                : repository.save(entity);
        return mapper.toDomain(persisted);
    }

    @Override
    @Transactional
    public PageResult<Task> findAll(Pagination pagination) {
        Pageable pageable = Pageable.from(
                pagination.page(),
                pagination.size(),
                Sort.of(Sort.Order.asc("createdAt"))
        );
        Page<TaskEntity> page = repository.findAll(pageable);

        return new PageResult<>(
                page.getContent().stream().map(mapper::toDomain).toList(),
                page.getTotalSize(),
                pagination.page(),
                pagination.size(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional
    public Optional<Task> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
