package ru.kata.spring.boot_security.demo.dto.request;

import java.util.Set;

public record UserCreateDTO(
        String email,
        String password,
        String firstName,
        String lastName,
        Integer age,
        Set<Long> roleIds
) {}