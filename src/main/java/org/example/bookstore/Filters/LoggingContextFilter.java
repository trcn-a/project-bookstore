package org.example.bookstore.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bookstore.Entities.User;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Фільтр для додавання контекстної інформації до логів (MDC).
 *
 * Цей фільтр зберігає інформацію про  користувача, сесію, а також IP-адресу запиту
 * у MDC (Mapped Diagnostic Context), що дозволяє використовувати ці дані для логування
 * у всіх наступних логах під час обробки поточного запиту.
 */
@Component
public class LoggingContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession(false);

            String sessionId = (session != null) ? session.getId() : "no-session";
            String user = "anonymous";

            if (session != null && session.getAttribute("user") != null) {
                User userObj = (User) session.getAttribute("user");
                user = userObj.getEmail();
            }

            MDC.put("sessionId", sessionId);
            MDC.put("user", user);
            MDC.put("ip", request.getRemoteAddr());

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
