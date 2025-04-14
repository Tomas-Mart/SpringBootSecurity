package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.dto.response.UserResponseDTO;
import ru.kata.spring.boot_security.demo.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        // Проверка CSRF токена
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken == null) {
            throw new AccessDeniedException("CSRF token not found");
        }

        return ResponseEntity.ok()
                .header(csrfToken.getHeaderName(), csrfToken.getToken())
                .body(userService.getUserByEmail(userDetails.getUsername()));
    }
}