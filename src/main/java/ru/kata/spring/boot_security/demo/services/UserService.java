package ru.kata.spring.boot_security.demo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.kata.spring.boot_security.demo.dto.request.UserCreateDTO;
import ru.kata.spring.boot_security.demo.dto.request.UserUpdateDTO;
import ru.kata.spring.boot_security.demo.dto.response.UserResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<UserResponseDTO> getAllUsers();
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO createUser(UserCreateDTO userDTO);
    UserResponseDTO updateUser(Long id, UserUpdateDTO userDTO);
    void deleteUser(Long id);
    List<String> getAllRoleNames();
    UserResponseDTO getUserByEmail(String email);
}