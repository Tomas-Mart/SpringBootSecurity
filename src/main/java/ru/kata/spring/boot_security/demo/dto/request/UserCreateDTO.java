package ru.kata.spring.boot_security.demo.dto.request;

import java.util.Set;

public record UserCreateDTO(
        String firstName,
        String lastName,
        Integer age,
        String email,
        String password,
        Set<Long> roleIds) {}
