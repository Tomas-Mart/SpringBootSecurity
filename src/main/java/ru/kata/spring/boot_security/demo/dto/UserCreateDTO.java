package ru.kata.spring.boot_security.demo.dto;


import java.util.List;

public record UserCreateDTO(
        String firstName,
        String lastName,
        Integer age,
        String email,
        String password,
        List<Long> roleIds) {}