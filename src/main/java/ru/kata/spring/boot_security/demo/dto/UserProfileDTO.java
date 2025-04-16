package ru.kata.spring.boot_security.demo.dto;

import jakarta.validation.constraints.*;

public record UserProfileDTO(
        Long id,
        @NotBlank @Size(min = 2, max = 50) String firstName,
        @NotBlank @Size(min = 2, max = 50) String lastName,
        @Min(1) @Max(120) Integer age,
        @Email @NotBlank String email,
        @Size(min = 3, max = 100) String newPassword,
        String currentPassword
) {}