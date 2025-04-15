// UserResponseDTO.java
package ru.kata.spring.boot_security.demo.dto;

import java.util.List;
import java.util.Set;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        Integer age,
        String email,
        String password, // может быть null при возврате данных
        Set<String> roles,
        List<UserResponseDTO> users, // для adminPanel
        List<String> allRoles // для adminPanel
) {
    // Конструктор для обычного ответа с пользователем
    public UserResponseDTO(Long id, String firstName, String lastName,
                           Integer age, String email, String password,
                           Set<String> roles) {
        this(id, firstName, lastName, age, email, password, roles, null, null);
    }
}