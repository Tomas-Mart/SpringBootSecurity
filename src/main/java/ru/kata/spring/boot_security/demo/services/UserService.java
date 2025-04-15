package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.dto.RoleDTO;
import ru.kata.spring.boot_security.demo.dto.UserCreateDTO;
import ru.kata.spring.boot_security.demo.dto.UserProfileDTO;
import ru.kata.spring.boot_security.demo.dto.UserResponseDTO;
import java.util.List;

public interface UserService {
    // Для аутентификации
    UserResponseDTO findByEmail(String email);

    // Основные CRUD операции
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO createUser(UserCreateDTO userDTO);
    UserResponseDTO updateUser(Long id, UserCreateDTO userDTO);
    void deleteUser(Long id);

    // Дополнительные методы
    List<String> getAllRoleNames();
    UserProfileDTO getUserProfile(String username);
    UserProfileDTO updateUserProfile(String username, UserProfileDTO userProfileDTO);
    List<RoleDTO> getRolesByIds(List<Long> ids);
}