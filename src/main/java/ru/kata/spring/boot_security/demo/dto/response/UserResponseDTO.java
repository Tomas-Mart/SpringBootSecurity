package ru.kata.spring.boot_security.demo.dto.response;

import java.util.Set;

public record UserResponseDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        Integer age,
        Set<String> roles
) {}