package ru.kata.spring.boot_security.demo.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserCreateDTO;
import ru.kata.spring.boot_security.demo.dto.UserResponseDTO;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO userDTO) {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserCreateDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
