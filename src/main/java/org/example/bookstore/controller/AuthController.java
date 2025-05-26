package org.example.bookstore.controller;

import jakarta.servlet.http.HttpSession;
import org.example.bookstore.entity.CartBook;
import org.example.bookstore.entity.User;
import org.example.bookstore.service.CartService;
import org.example.bookstore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


/**
 * Контролер для обробки реєстрації та входу.
 */
@Controller
@RequestMapping("/")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    private final CartService cartService;

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Конструктор контролера, що інʼєктує сервіси для роботи з користувачами, відгуками та замовленнями.
     *
     * @param userService сервіс для роботи з користувачами
     * @param reviewService сервіс для роботи з відгуками
     * @param orderService сервіс для роботи з замовленнями
     */
    @Autowired
    public AuthController(UserService userService, CartService cartService, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.cartService = cartService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Показує форму для реєстрації нового користувача.
     *
     * @return назва HTML-шаблону "register"
     */
    @GetMapping("/register")
    public String showRegistrationForm() {
        logger.info("Displaying registration form");
        return "register";
    }

    /**
     * Показує форму для входу користувача.
     *
     * @return назва HTML-шаблону "login"
     */
    @GetMapping("/login")
    public String showLoginForm() {
        logger.info("Displaying login form");
        return "login";
    }

    /**
     * Обробляє реєстрацію нового користувача.
     *
     * @param firstName імʼя користувача
     * @param lastName прізвище користувача
     * @param email електронна пошта користувача
     * @param password пароль користувача
     * @param confirmPassword підтвердження пароля
     * @param session HTTP-сесія користувача
     * @param model модель для передачі даних у шаблон
     * @return назва HTML-шаблону або редирект
     */
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            HttpSession session,  RedirectAttributes redirectAttributes,
            Model model) {

        logger.info("Processing registration for user: {}", email);
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("confirmPassword", confirmPassword);


        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Password and password confirmation do not match");
            logger.warn("Registration failed: Password and confirmation do not match for {}", email);

            return "register";
        }

        try {
            User user = userService.registerUser(firstName, lastName, email, password);

            List<CartBook> guestCart = (List<CartBook>) session.getAttribute("guestCart");

            if (guestCart != null && !guestCart.isEmpty()) {
                cartService.mergeGuestCartWithUserCart(guestCart,user);
                session.removeAttribute("guestCart");
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            if (userDetails == null) {
                throw new UsernameNotFoundException("User not found after registration");
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails.getUsername(), password);

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            return "redirect:/";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Помилка при реєстрації: " + e.getMessage());
            return "redirect:/register";
        }

    }
}