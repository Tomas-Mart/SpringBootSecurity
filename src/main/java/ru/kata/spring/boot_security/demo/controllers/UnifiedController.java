package ru.kata.spring.boot_security.demo.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.request.*;
import ru.kata.spring.boot_security.demo.dto.response.*;
import ru.kata.spring.boot_security.demo.security.JwtTokenUtil;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UnifiedController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public UnifiedController(UserService userService,
                             AuthenticationManager authenticationManager,
                             JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // ========== Аутентификация ==========
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()));

        final UserDetails user = userService.loadUserByUsername(request.username());
        return ResponseEntity.ok(new AuthResponse(jwtTokenUtil.generateToken(user)));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
        String token = jwtTokenUtil.parseJwt(request);
        if (token != null && jwtTokenUtil.validateToken(token)) {
            return ResponseEntity.ok(new AuthResponse(jwtTokenUtil.refreshToken(token)));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // ========== Пользовательские операции ==========
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<UserResponseDTO> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken == null) {
            throw new AccessDeniedException("CSRF token not found");
        }

        return ResponseEntity.ok()
                .header(csrfToken.getHeaderName(), csrfToken.getToken())
                .body(userService.getUserByEmail(userDetails.getUsername()));
    }

    // ========== Административные операции ==========
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "email") String sortBy) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            return ResponseEntity.ok(userService.getAllUsers(pageable));
        } catch (PropertyReferenceException e) {
            return ResponseEntity.badRequest().body("Invalid sort parameter");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        return ResponseEntity.ok(userService.getAllRoleNames());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody @Valid UserCreateDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(userDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ========== Обработка ошибок ==========
    @GetMapping("/error")
    public ResponseEntity<Map<String, String>> handleError() {
        Map<String, String> errorInfo = new HashMap<>();
        errorInfo.put("title", "Error 500");
        errorInfo.put("message", "Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
    }

    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
    public ResponseEntity<?> handleAuthExceptions() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}