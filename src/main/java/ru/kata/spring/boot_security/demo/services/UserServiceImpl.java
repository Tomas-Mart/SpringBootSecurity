package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.RoleDTO;
import ru.kata.spring.boot_security.demo.dto.UserCreateDTO;
import ru.kata.spring.boot_security.demo.dto.UserResponseDTO;
import ru.kata.spring.boot_security.demo.dto.UserProfileDTO;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserResponseDTO findByEmail(String email) {
        User user = (User) loadUserByUsername(email);
        return toUserResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toUserResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreateDTO userDTO) {
        User user = new User();
        updateUserFromDTO(user, userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        return toUserResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserCreateDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        updateUserFromDTO(user, userDTO);
        if (userDTO.password() != null && !userDTO.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.password()));
        }
        return toUserResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserProfileDTO updateUserProfile(String email, UserProfileDTO userProfileDTO) {
        User user = (User) loadUserByUsername(email);

        user.setFirstName(userProfileDTO.firstName());
        user.setLastName(userProfileDTO.lastName());
        user.setAge(userProfileDTO.age());

        if (userProfileDTO.newPassword() != null && !userProfileDTO.newPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userProfileDTO.newPassword()));
        }

        return new UserProfileDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getEmail(),
                null,
                null
        );
    }

    private UserResponseDTO toUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getEmail(),
                null,
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                null,
                null
        );
    }

    private void updateUserFromDTO(User user, UserCreateDTO userDTO) {
        user.setFirstName(userDTO.firstName());
        user.setLastName(userDTO.lastName());
        user.setAge(userDTO.age());
        user.setEmail(userDTO.email());

        if (userDTO.roles() != null && !userDTO.roles().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(userDTO.roles()));
            user.setRoles(roles);
        }
    }
}