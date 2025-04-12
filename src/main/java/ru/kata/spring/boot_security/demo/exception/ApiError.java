package ru.kata.spring.boot_security.demo.exception;

public record ApiError(
        int status,
        String message,
        String debugMessage
) {}