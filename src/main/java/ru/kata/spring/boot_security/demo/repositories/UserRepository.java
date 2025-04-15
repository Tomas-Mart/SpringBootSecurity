package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT u FROM User u WHERE u.email = :email")
    @NonNull
    Optional<User> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {"roles"})
    @Override
    @NonNull
    List<User> findAll();
}