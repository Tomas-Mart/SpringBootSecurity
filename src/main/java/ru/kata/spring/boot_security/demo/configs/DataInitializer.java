package ru.kata.spring.boot_security.demo.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Создание ролей
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
        Role userRole = createRoleIfNotExists("ROLE_USER");
        // Создание администратора
        createUserIfNotExists(
                "admin@example.com",
                "admin",
                30,
                "Admin",
                "Admin",
                Set.of(adminRole, userRole)
        );

        // Создание обычного пользователя
        createUserIfNotExists(
                "user@example.com",
                "user",
                25,
                "User",
                "User",
                Set.of(userRole)
        );
    }

    @Transactional
    protected Role createRoleIfNotExists(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role(roleName);
                    // Для каскадирования при необходимости
                    return roleRepository.saveAndFlush(newRole);
                });
    }

    @Transactional
    protected void createUserIfNotExists(String email,
                                         String password,
                                         int age,
                                         String firstName,
                                         String lastName,
                                         Set<Role> roles) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setAge(age);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRoles(new HashSet<>(roles)); // Создаем копию для безопасности

            userRepository.saveAndFlush(user);
        }
    }
}