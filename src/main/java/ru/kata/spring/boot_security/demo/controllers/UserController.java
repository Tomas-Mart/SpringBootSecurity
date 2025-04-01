package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String userPage(Principal principal, Model model) {
        try {
            User user = userService.findByUsername(principal.getName());
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            model.addAttribute("user", user);
            return "user";
        } catch (UsernameNotFoundException ex) {
            model.addAttribute("error", "User not found: " + ex.getMessage());
            return "error";
        } catch (Exception ex) {
            model.addAttribute("error", "Error loading user data: " + ex.getMessage());
            return "error";
        }
    }
}