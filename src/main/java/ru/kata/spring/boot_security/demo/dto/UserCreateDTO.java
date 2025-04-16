package ru.kata.spring.boot_security.demo.dto;

import jakarta.validation.constraints.*;

import java.util.Set;

public record UserCreateDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Min(1) @Max(120) Integer age,
        @Email @NotBlank String email,
        @Size(min = 3) String password,
        Set<Long> roles
) {}