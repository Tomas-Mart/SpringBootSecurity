package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Отключение защиты от clickjacking (можно включить если нужно)
                .headers().frameOptions().disable()
                .and()

                // Настройка CSRF защиты
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringAntMatchers(
                                "/api/auth/login",
                                "/api/rest/users/**",
                                "/api/rest/users",
                                "/logout"
                        )
                )

                // Настройка авторизации
                .authorizeRequests(auth -> auth
                        // Публичные ресурсы
                        .antMatchers(
                                "/",
                                "/login",
                                "/error",
                                "/webjars/**",
                                "/static/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()

                        // API endpoints
                        .antMatchers(HttpMethod.GET, "/api/rest/current-user").authenticated()
                        .antMatchers(HttpMethod.POST, "/api/rest/users").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/api/rest/users/**").hasRole("ADMIN")

                        // Страницы приложения
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")

                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )

                // Настройка формы входа
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/user", true)
                        .successHandler(new SuccessUserHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // Настройка выхода из системы
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )

                // Обработка ошибок доступа
                .exceptionHandling(handling -> handling
                        .accessDeniedPage("/access-denied")
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Используем надежный алгоритм хеширования с высокой стоимостью
        return new BCryptPasswordEncoder(12);
    }
}