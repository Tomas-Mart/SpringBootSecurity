package ru.kata.spring.boot_security.demo.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.AuthStatusDTO;
import ru.kata.spring.boot_security.demo.dto.MessageDTO;

@RestController
@RequestMapping("/api")
public class MainController {

    @GetMapping("/auth/check")
    public ResponseEntity<AuthStatusDTO> checkAuthStatus(Authentication authentication) {
        AuthStatusDTO response = new AuthStatusDTO();

        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        response.setAuthenticated(isAuthenticated);

        if (isAuthenticated) {
            response.setEmail(authentication.getName());

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority ->
                            grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            response.setAdmin(isAdmin);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDTO> logout(HttpServletRequest request) {
        try {
            request.logout();
            return ResponseEntity.ok(new MessageDTO("Вы успешно вышли из системы"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDTO("Ошибка при выходе из системы: " + e.getMessage()));
        }
    }

    @GetMapping("/access-denied")
    public ResponseEntity<MessageDTO> accessDenied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new MessageDTO("Доступ запрещен"));
    }
}