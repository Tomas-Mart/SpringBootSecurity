package ru.kata.spring.boot_security.demo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.request.UserCreateDTO;
import ru.kata.spring.boot_security.demo.dto.request.UserUpdateDTO;
import ru.kata.spring.boot_security.demo.dto.response.UserResponseDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

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
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public UserResponseDTO createUser(UserCreateDTO dto) {
        User user = new User();
        updateUserFields(user, dto);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (dto.firstName() != null) user.setFirstName(dto.firstName());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
        if (dto.age() != null) user.setAge(dto.age());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.password() != null && !dto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        if (dto.roleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllByIdIn(dto.roleIds()));
            user.setRoles(roles);
        }
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<String> getAllRoleNames() {
        return roleRepository.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    // Вспомогательные методы
    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(Role::getName) // Получаем названия ролей
                        .collect(Collectors.toSet())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void updateUserFields(User user, UserCreateDTO dto) {
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setAge(dto.age());
        user.setEmail(dto.email());
        if (dto.password() != null && !dto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        // Преобразуем List<Role> в Set<Role>
        Set<Role> roles = new HashSet<>(roleRepository.findAllByIdIn(dto.roleIds()));
        user.setRoles(roles);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}
