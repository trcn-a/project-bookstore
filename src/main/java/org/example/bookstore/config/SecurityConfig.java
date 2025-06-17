package org.example.bookstore.config;

import jakarta.servlet.http.HttpSession;
import org.example.bookstore.entity.CartBook;
import org.example.bookstore.entity.User;
import org.example.bookstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private CartService cartService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cart/**", "/search/**", "/login", "/book/**", "/author/**", "/register", "/css/**", "/js/**", "/favicon/**", "/images/**",  "/").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/process-login")
                        .successHandler((request, response, authentication) -> {
                            var authorities = authentication.getAuthorities();
                            String redirectUrl = "/";

                            for (var authority : authorities) {
                                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                                    redirectUrl = "/admin/books";
                                    break;
                                } else if (authority.getAuthority().equals("ROLE_USER")) {
                                    redirectUrl = "/";
                                }
                            }

                            HttpSession session = request.getSession();
                            List<CartBook> guestCart = (List<CartBook>) session.getAttribute("guestCart");

                            if (guestCart != null && !guestCart.isEmpty()) {
                                User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
                                cartService.mergeGuestCartWithUserCart(guestCart, user);
                                session.setAttribute("cartQuantity",  cartService.getTotalQuantityForUserCart(user));

                                session.removeAttribute("guestCart");
                            }

                            response.sendRedirect(redirectUrl);
                        })

                        .failureUrl("/login?error=true")
                .permitAll()
        )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

