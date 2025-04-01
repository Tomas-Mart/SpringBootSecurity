package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User findByUsername(String username);
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(Long id);
}