package ru.kata.spring.boot_security.demo.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ErrorController {

    @GetMapping("/api/error")
    public ResponseEntity<Map<String, String>> getErrorDetails(
            HttpServletRequest request) {

        Map<String, String> errorInfo = new HashMap<>();
        errorInfo.put("title", "Error 500");
        errorInfo.put("message", "Internal Server Error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorInfo);
    }
}
