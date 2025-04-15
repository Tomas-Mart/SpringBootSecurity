package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.kata.spring.boot_security.demo.models.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Override
    @EntityGraph(attributePaths = {"users"})
    @NonNull
    Optional<Role> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"users"})
    @NonNull
    Optional<Role> findByName(@NonNull String name);
}