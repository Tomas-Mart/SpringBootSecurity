package ru.kata.spring.boot_security.demo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Min(value = 1, message = "Age should be at least 1")
    @Max(value = 120, message = "Age should be less than 120")
    private Integer age;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    public UserProfileDTO(
            Long id, String firstName,
            String lastName,
            Integer age,
            String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
    }

    @Size(min = 3, max = 100, message = "Password must be between 3 and 100 characters")
    private String newPassword;

    private String currentPassword;
}