package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final LoginSuccessHandler successUserHandler;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public WebSecurityConfig(LoginSuccessHandler successUserHandler,
                             UserDetailsService userDetailsService,
                             UserRepository userRepository,
                             RoleRepository roleRepository) {
        this.successUserHandler = successUserHandler;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Отключаем CSRF для упрощения (можно включить для production)
                .authorizeRequests()
                .antMatchers("/", "/index", "/login", "/favicon.ico",
                        "/css/**", "/js/**", "/images/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login") // Указываем нашу кастомную страницу входа
                .defaultSuccessUrl("/")
                .successHandler((request, response, authentication) -> {
                    if (authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        response.sendRedirect("/admin");
                    } else {
                        response.sendRedirect("/");
                    }
                })
                .permitAll()
                    .loginProcessingUrl("/perform_login") // URL для обработки формы входа
                .defaultSuccessUrl("/user", true) // Перенаправление после успешного входа
                .failureUrl("/login?error=true") // Перенаправление при ошибке
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout") // URL для выхода
                .logoutSuccessUrl("/login?logout") // Перенаправление после выхода
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/access-denied");

        return http.build();
    }

    // Остальные методы остаются без изменений
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Value("${app.default.admin.password}")
    private String defaultAdminPassword;

    @Value("${app.default.user.password}")
    private String defaultUserPassword;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            PasswordEncoder encoder = passwordEncoder();
            System.out.println("Admin password hash: " + encoder.encode("admin"));
            System.out.println("User password hash: " + encoder.encode("user"));
            if (userRepository.count() == 0 && roleRepository.count() == 0) {
                // Создаем роли
                Role adminRole = new Role("ROLE_ADMIN");
                Role userRole = new Role("ROLE_USER");
                roleRepository.saveAll(List.of(adminRole, userRole));

                // Пароли можно задать здесь
                String adminPassword = "admin";
                String userPassword = "user";

                // Создаем администратора
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode(adminPassword));
                admin.setRoles(Collections.singleton(adminRole));
                userRepository.save(admin);

                // Создаем обычного пользователя
                User user = new User();
                user.setUsername("user");
                user.setPassword(encoder.encode(userPassword));
                user.setRoles(Collections.singleton(userRole));
                userRepository.save(user);

                // Выводим информацию в консоль
                System.out.println("\n=======================================");
                System.out.println("Тестовые пользователи созданы:");
                System.out.println("Администратор:");
                System.out.println("Логин: admin");
                System.out.println("Пароль: " + adminPassword);
                System.out.println("\nОбычный пользователь:");
                System.out.println("Логин: user");
                System.out.println("Пароль: " + userPassword);
                System.out.println("=======================================\n");
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}