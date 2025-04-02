package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.models.Role;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    Optional<User> findByUsername(String username);
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(Long id);
    void addRoleToUser(Long userId, Role role)
    throws UsernameNotFoundException, IllegalArgumentException;
}