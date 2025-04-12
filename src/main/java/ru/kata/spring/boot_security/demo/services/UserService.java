package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.dto.request.UserCreateDTO;
import ru.kata.spring.boot_security.demo.dto.response.UserResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO createUser(UserCreateDTO userDTO);
    UserResponseDTO updateUser(Long id, UserCreateDTO userDTO);
    void deleteUser(Long id);
    List<String> getAllRoleNames();
    UserResponseDTO getUserByEmail(String email);
}