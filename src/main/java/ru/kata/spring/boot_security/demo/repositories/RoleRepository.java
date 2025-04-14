package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.spring.boot_security.demo.models.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @EntityGraph(attributePaths = {"users"})
    List<Role> findAllByIdIn(Collection<Long> ids);

    @EntityGraph(attributePaths = {"users"})
    Optional<Role> findByName(String name);
}