package ru.kata.spring.boot_security.demo.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                    loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok().build();

        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            logger.error("Authentication error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error");
        }
    }

    @GetMapping("/auth/check")
    public ResponseEntity<AuthStatusDTO> checkAuthStatus(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            String email = authentication.getName();
            return ResponseEntity.ok(new AuthStatusDTO(true, email, isAdmin));
        }
        return ResponseEntity.ok(new AuthStatusDTO(false, null, false));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @Data
    @AllArgsConstructor
    public static class AuthStatusDTO {
        private boolean authenticated;
        private String email;
        private boolean admin;
    }

    @Getter
    @Setter
    public static class LoginRequest {
        private String email;
        private String password;
    }
}
