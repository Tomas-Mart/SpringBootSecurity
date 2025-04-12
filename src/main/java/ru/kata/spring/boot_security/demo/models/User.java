package ru.kata.spring.boot_security.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;


import java.util.*;
import java.util.stream.Collectors;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true)
    @NotBlank(message = "{user.email.notblank}")
    @Size(min = 5, max = 100, message = "{user.email.size}")
    private String email;

    @Column
    @NotBlank(message = "{user.password.notblank}")
    @Size(min = 5, message = "{user.password.size}")
    @JsonIgnore
    private String password;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @NotEmpty(message = "{user.roles.notempty}")
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Удален явный конструктор по умолчанию (заменен аннотациями Lombok)

    // Удален явный конструктор с параметрами (заменен аннотациями Lombok)

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
        return roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    // Упрощенные методы добавления ролей
    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void addRoles(Collection<Role> roles) {
        this.roles.addAll(roles);
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String getUsername() { return email; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
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
                ", roles=" + roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList()) +
                '}';
    }
}