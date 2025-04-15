package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.RoleDTO;
import ru.kata.spring.boot_security.demo.dto.UserCreateDTO;
import ru.kata.spring.boot_security.demo.dto.UserProfileDTO;
import ru.kata.spring.boot_security.demo.dto.UserResponseDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllRoleNames() {
        return roleRepository.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    @Override
    public UserProfileDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserProfileDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getEmail()
        );
    }


    @Override
    public UserProfileDTO updateUserProfile(String email, UserProfileDTO userProfileDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFirstName(userProfileDTO.getFirstName());
        user.setLastName(userProfileDTO.getLastName());
        user.setAge(userProfileDTO.getAge());

        // Обновляем email только если он изменился
        if (!user.getEmail().equals(userProfileDTO.getEmail())) {
            user.setEmail(userProfileDTO.getEmail());
        }

        // Обработка пароля, если он был предоставлен
        if (userProfileDTO.getNewPassword() != null && !userProfileDTO.getNewPassword().isEmpty()) {
            if (passwordEncoder.matches(userProfileDTO.getCurrentPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(userProfileDTO.getNewPassword()));
            } else {
                throw new SecurityException("Current password is incorrect");
            }
        }

        userRepository.save(user);
        return new UserProfileDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getEmail()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserResponseDTO createUser(UserCreateDTO userDTO) {
        User user = new User();
        updateUserFromDTO(user, userDTO);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserCreateDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        updateUserFromDTO(user, userDTO);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Используем email как username
                user.getPassword(),
                user.getAuthorities()
        );
    }

    private void updateUserFromDTO(User user, UserCreateDTO dto) {
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setAge(dto.age());
        user.setEmail(dto.email());

        if (dto.password() != null && !dto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        if (dto.roleIds() != null && !dto.roleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(
                    roleRepository.findAllByIdIn(dto.roleIds())
            );
            user.setRoles(roles);
        }
    }

    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getEmail(),
                null, // Пароль не возвращаем в DTO
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public List<RoleDTO> getRolesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return roleRepository.findAllByIdIn(ids).stream()
                .map(this::convertToRoleDTO)
                .collect(Collectors.toList());
    }

    private RoleDTO convertToRoleDTO(Role role) {
        return new RoleDTO(
                role.getId(),
                role.getName().replace("ROLE_", "")
        );
    }
}