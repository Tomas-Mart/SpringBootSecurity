package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.util.*;

@Controller
public class GlobalRestController {

    private final UserService userService;
    private final UserRepository userRepository;

    public GlobalRestController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // Все пользователи
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/rest")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    // Текущий аутентифицированный пользователь
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/rest/current-user")
    @ResponseBody
    public Optional<User> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    // Получение пользователя по его ID
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @ResponseBody
    @GetMapping("/api/rest/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    // Отправка пользователя
    @PostMapping("/api/rest")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody @Valid User user) {
        try {
            User savedUser = userService.save(user);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedUser);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Удаление пользователя по его ID
    @DeleteMapping("/api/rest/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Обновление пользователя по его ID
    @ResponseBody
    @PutMapping("/api/rest/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody @Valid User user, @PathVariable Long id, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Пропускаем ошибки валидации для пустого пароля
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                if (!"password".equals(error.getField())) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
            });

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }
        }
        User updateUser = userService.update(user, id);
        return ResponseEntity.ok(updateUser);
    }

    // Получение пользователей в зависимости от роли аутентифицированного пользователя
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/rest/visible-users")
    public ResponseEntity<List<User>> getVisibleUsers(Authentication authentication) {
        Optional<User> currentUser = userRepository.findByUsername(authentication.getName());
        if (currentUser.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        boolean isAdmin = currentUser.get().getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(userRepository.findAll());
        } else {
            return ResponseEntity.ok(Collections.singletonList(currentUser.get()));
        }
    }
}