package ru.kata.spring.boot_security.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.*;
import ru.kata.spring.boot_security.demo.services.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public UserResponseDTO getProfile(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }

    @PutMapping("/profile")
    public UserProfileDTO updateProfile(@RequestBody UserProfileDTO dto, Authentication authentication) {
        return userService.updateUserProfile(authentication.getName(), dto);
    }
}