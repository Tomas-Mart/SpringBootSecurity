package ru.kata.spring.boot_security.demo.dto;

import java.util.Set;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        Integer age,
        String email,
        String password,
        Set<String> roles
) {}