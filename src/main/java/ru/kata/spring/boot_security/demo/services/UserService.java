package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.dto.RoleDTO;
import ru.kata.spring.boot_security.demo.dto.UserCreateDTO;
import ru.kata.spring.boot_security.demo.dto.UserResponseDTO;
import ru.kata.spring.boot_security.demo.dto.UserProfileDTO;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponseDTO findByEmail(String email);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO createUser(UserCreateDTO userDTO);
    UserResponseDTO updateUser(Long id, UserCreateDTO userDTO);
    void deleteUser(Long id);
    List<RoleDTO> getAllRoles();
    UserProfileDTO updateUserProfile(String email, UserProfileDTO userProfileDTO);
}