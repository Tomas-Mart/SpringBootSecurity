package ru.kata.spring.boot_security.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "{user.firstName.notblank}")
    @Size(min = 2, max = 50, message = "{user.firstName.size}")
    private String firstName;

    @Column
    @NotBlank(message = "{user.lastName.notblank}")
    @Size(min = 2, max = 50, message = "{user.lastName.size}")
    private String lastName;

    @Column
    @NotNull(message = "{user.age.notnull}")
    @Min(value = 0, message = "{user.age.min}")
    private Integer age;

    @Email
    @Column(unique = true)
    @NotBlank(message = "{user.email.notblank}")
    @Size(min = 5, max = 100, message = "{user.email.size}")
    private String email;

    @Column
    @NotBlank(message = "{user.password.notblank}")
    @Size(min = 5, message = "{user.password.size}")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @NotEmpty(message = "{user.roles.notempty}")
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(Long id, String firstName, String lastName, Integer age, String email, String password, Set<Role> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean hasRole(String roleName) {
        return roles != null && roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    public boolean addRole(Role role) {
        if (role == null) return false;
        return roles.stream().noneMatch(r -> r.equals(role)) && roles.add(role);
    }

    public boolean addRoles(Set<Role> roles) {
        if (roles == null) return false;
        return this.roles.addAll(roles);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", password='" + password + '\'' +
                '}';
    }
}