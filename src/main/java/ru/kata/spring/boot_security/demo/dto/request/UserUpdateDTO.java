package ru.kata.spring.boot_security.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserUpdateDTO(
        @Email(message = "Некорректный формат email")
        String email,

        @Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 символов")
        String firstName,

        @Size(min = 2, max = 30, message = "Фамилия должна быть от 2 до 30 символов")
        String lastName,

        @Min(value = 1, message = "Возраст должен быть положительным числом")
        Integer age,

        @Size(min = 4, message = "Пароль должен содержать минимум 4 символа")
        String password,  // Опциональное поле

        List<Long> roleIds) {
}