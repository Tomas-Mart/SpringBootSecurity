package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.kata.spring.boot_security.demo.models.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @EntityGraph(attributePaths = {"users"})
    @NonNull
    Optional<Role> findByName(String name);

    @EntityGraph(attributePaths = {"users"})
    @NonNull
    List<Role> findAllByIdIn(@NonNull Collection<Long> ids);
}