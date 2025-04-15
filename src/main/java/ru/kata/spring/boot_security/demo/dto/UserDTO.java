package ru.kata.spring.boot_security.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {

    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 3, message = "Password must be at least 3 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Min(value = 0, message = "Age should be positive")
    private Integer age;

    private Set<Long> roleIds;
}
