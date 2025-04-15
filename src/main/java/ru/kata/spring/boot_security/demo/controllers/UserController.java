package ru.kata.spring.boot_security.demo.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserProfileDTO;
import ru.kata.spring.boot_security.demo.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserProfileDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDTO userProfile = userService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDTO userProfile = userService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UserProfileDTO userProfileDTO) {
        UserProfileDTO updatedProfile = userService.updateUserProfile(userDetails.getUsername(), userProfileDTO);
        return ResponseEntity.ok(updatedProfile);
    }
}