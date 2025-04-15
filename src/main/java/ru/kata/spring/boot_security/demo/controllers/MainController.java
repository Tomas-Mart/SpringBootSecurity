package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String homePage() {
        return "redirect:/login"; // Перенаправление на страницу входа
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) Boolean error,
            @RequestParam(required = false) Boolean logout,
            Model model
    ) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("error", "Неверные учетные данные");
        }
        if (Boolean.TRUE.equals(logout)) {
            model.addAttribute("message", "Вы успешно вышли из системы");
        }
        return "login"; // Возвращаем шаблон login.html
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // Возвращаем шаблон admin.html
    }
}
