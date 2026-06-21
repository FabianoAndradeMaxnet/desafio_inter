package com.example.todo.domain;

public record Pagination(int page, int size) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    public Pagination {
        if (page < 0) {
            throw new DomainValidationException("Page index cannot be negative");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new DomainValidationException("Page size must be between 1 and 100");
        }
    }

    public static Pagination of(int page, int size) {
        return new Pagination(page, size);
    }
}
