package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        String targetUrl = request.getParameter("target");
        if (targetUrl != null && !targetUrl.isEmpty()) {
            // Проверка прав для целевого URL
            if ((targetUrl.startsWith("/admin") && roles.contains("ROLE_ADMIN")) ||
                    targetUrl.startsWith("/user")) {
                response.sendRedirect(targetUrl);
                return;
            }
        }

        // Стандартное перенаправление
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin");
        } else {
            response.sendRedirect("/user");
        }
    }
}