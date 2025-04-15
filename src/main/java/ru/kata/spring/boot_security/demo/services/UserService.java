package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.dto.UserCreateDTO;
import ru.kata.spring.boot_security.demo.dto.UserResponseDTO;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<UserResponseDTO> getAllUsers();
    List<String> getAllRoleNames();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByEmail(String email);
    UserResponseDTO createUser(UserCreateDTO userDTO);
    UserResponseDTO updateUser(Long id, UserCreateDTO userDTO);
    void deleteUser(Long id);
}
