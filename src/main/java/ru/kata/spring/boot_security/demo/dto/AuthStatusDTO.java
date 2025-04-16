package ru.kata.spring.boot_security.demo.dto;

public record AuthStatusDTO(
        boolean authenticated,
        String email,
        boolean admin
) {}