package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String adminPanel(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + userDetails.getUsername()));

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", userService.getAllRoles());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("newUser", new User());
        return "admin";
    }

    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("isAdminView", true);
        return "user"; // Используем тот же шаблон user.html
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("newUser") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam("roles") List<Long> roleIds,
                             Model model) {

        if (bindingResult.hasErrors()) {
            return prepareModel(model);
        }

        try {
            userService.saveUser(user, roleIds);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("username", "error.username", e.getMessage());
            return prepareModel(model);
        } catch (Exception e) {
            bindingResult.reject("error.global", "An error occurred while saving the user.");
            return prepareModel(model);
        }
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam("id") Long id,
                             @RequestParam("username") String username,
                             @RequestParam(value = "password", required = false) String password,
                             @RequestParam("roles") List<Long> roleIds) {

        User user = userService.getUserById(id);
        user.setUsername(username);

        if (password != null && !password.isEmpty()) {
            user.setPassword(password);
        }

        userService.updateUserRoles(id, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    private String prepareModel(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", userService.getAllRoles());
        return "admin";
    }
}