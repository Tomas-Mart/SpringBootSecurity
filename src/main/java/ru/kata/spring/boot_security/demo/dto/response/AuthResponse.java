package ru.kata.spring.boot_security.demo.dto.response;

public class AuthResponse {
    private String token;

    // конструктор/геттер

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
