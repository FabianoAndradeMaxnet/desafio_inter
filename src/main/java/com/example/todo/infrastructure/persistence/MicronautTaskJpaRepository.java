package com.example.todo.infrastructure.persistence;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;

import java.util.UUID;

@Repository
interface MicronautTaskJpaRepository extends PageableRepository<TaskEntity, UUID> {
}
