package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(@Valid User user);
    User updateUser(@Valid User user, Long id);
    void deleteUser(Long id);
    Optional<User> findByUsername(String username);
}