package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.models.Role;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    Optional<User> findByUsername(String username);
    List<User> getAllUsers();
    List<Role> getAllRoles();
    User getUserById(Long id);
    void createUserWithRoles(User user, List<Long> roleIds);
    void updateUserWithRoles(Long id, String username, String password, List<Long> roleIds);
    void deleteUser(Long id);
    boolean existsByUsername(String username);


    void saveUser(User user);
    void updateUser(User user, List<Long> roleIds);
    void addRoleToUser(Long userId, Role role);
    void addRolesToUser(Long userId, Set<Role> roles);
    boolean userHasRole(Long userId, String roleName);
    void grantUserRole(Long userId, Role role);
    void registerNewUser(User user);
}