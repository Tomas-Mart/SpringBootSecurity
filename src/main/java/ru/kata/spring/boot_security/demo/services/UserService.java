package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.models.Role;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    List<User> getAllUsers();
    List<Role> getAllRoles();
    User getUserById(Long id);
    void createUserWithRoles(User user, List<Long> roleIds);
    void updateUserWithRoles(Long id, String firstName, String lastName, Integer age, String email, List<Long> roleIds, String password);
    void deleteUser(Long id);
}
