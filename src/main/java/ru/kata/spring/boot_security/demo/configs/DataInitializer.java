package ru.kata.spring.boot_security.demo.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

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
                "Admin",
                "Admin",
                30,
                "admin@example.com",
                "admin",
                Set.of(adminRole, userRole)
        );

        // Создание обычного пользователя
        createUserIfNotExists(
                "User",
                "User",
                25,
                "user@example.com",
                "user",
                Set.of(userRole)
        );
    }

    @Transactional
    protected Role createRoleIfNotExists(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role(roleName);
                    return roleRepository.save(newRole); // Используем save вместо saveAndFlush
                });
    }

    @Transactional
    protected void createUserIfNotExists(
            String firstName,
            String lastName,
            int age,
            String email,
            String password,
            Set<Role> roles) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(new HashSet<>(roles)); // Создаем копию для безопасности

            userRepository.save(user); // Используем save вместо saveAndFlush
        }
    }
}
