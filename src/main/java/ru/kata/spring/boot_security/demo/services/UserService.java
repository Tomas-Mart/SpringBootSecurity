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
    void deleteUser(Long id);
    void saveUser(User user, List<Long> roleIds);
    void updateUserRoles(Long userId, List<Long> roleIds);
    boolean userHasRole(Long userId, String roleName);
}