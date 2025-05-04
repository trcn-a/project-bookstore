package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Entities.Order;
import org.example.bookstore.Entities.OrderedBook;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.CartService;
import org.example.bookstore.Services.OrderService;
import org.example.bookstore.Services.ReviewService;
import org.example.bookstore.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;


/**
 * Контролер для обробки запитів, пов'язаних з профілем користувача.
 * Відповідає за реєстрацію, вхід, перегляд та редагування  особистих даних,
 * перегляд замовлень і відгуків користувача.
 */
@Controller
@RequestMapping("/")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    private final CartService cartService;

    /**
     * Конструктор контролера, що інʼєктує сервіси для роботи з користувачами, відгуками та замовленнями.
     *
     * @param userService сервіс для роботи з користувачами
     * @param reviewService сервіс для роботи з відгуками
     * @param orderService сервіс для роботи з замовленнями
     */
    @Autowired
    public AuthController(UserService userService,  CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
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
            HttpSession session,
            Model model) {

        logger.info("Processing registration for user: {}", email);
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("confirmPassword", confirmPassword);

        // Перевірка валідності даних
        if (firstName == null || firstName.isBlank()) {
            model.addAttribute("error", "First name is required");
            logger.warn("Registration failed: First name is required for {}", email);

            return "register";
        }
        if (lastName == null || lastName.isBlank()) {
            model.addAttribute("error", "Last name is required");
            logger.warn("Registration failed: Last name is required for {}", email);

            return "register";
        }
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            logger.warn("Registration failed: Invalid email format for {}", email);

            model.addAttribute("error", "Email should be valid");
            return "register";
        }
        if (password == null || password.length() < 8 ||
                !password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*")) {
            model.addAttribute("error",
                    "Password must be at least 8 characters long, including at " +
                            "least one uppercase letter, one lowercase letter, one digit, and one special character");
            logger.warn("Registration failed: Password does not meet criteria for {}", email);

            return "register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Password and password confirmation do not match");
            logger.warn("Registration failed: Password and confirmation do not match for {}", email);

            return "register";
        }

        try {
            User user = userService.registerUser(firstName, lastName, email, password);
            session.setAttribute("user", user);
            logger.info("User registered successfully: {}", email);
            List<CartBook> guestCart = (List<CartBook>) session.getAttribute("guestCart");

            if (guestCart != null && !guestCart.isEmpty()) {
                cartService.mergeGuestCartWithUserCart(guestCart, user);
                session.removeAttribute("guestCart"); // Очистити гостьовий кошик
            }
            return "redirect:/";
        } catch (Exception ex) {
            throw new RuntimeException("Error during user registration for email: " + email, ex);
        }
    }

    /**
     * Обробляє вхід користувача в систему.
     *
     * @param email електронна пошта користувача
     * @param password пароль користувача
     * @param session HTTP-сесія користувача
     * @param model модель для передачі даних у шаблон
     * @param request HTTP-запит
     * @return назва HTML-шаблону або редирект
     */
    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model,
            HttpServletRequest request
    ) {
        logger.info("Attempting to log in user: {}", email);

        model.addAttribute("email", email);
        model.addAttribute("password", password);

        // Перевірка валідності email
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {

            model.addAttribute("error", "Email should be valid");
            logger.warn("Login failed: Invalid email format for {}", email);

            return "login";
        }

        try {
            User user = userService.authenticateUser(email, password);
            session.setAttribute("user", user);
            logger.info("User successfully logged in: {}", email);

            List<CartBook> guestCart = (List<CartBook>) session.getAttribute("guestCart");

            if (guestCart != null && !guestCart.isEmpty()) {
                cartService.mergeGuestCartWithUserCart(guestCart, user);
                session.removeAttribute("guestCart"); // Очистити гостьовий кошик
            }
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            UUID errorId = UUID.randomUUID();
            logger.error("Order creation error [{}] for email{}: {}", errorId, email, ex.getMessage());
            return "login";
        }
    }


    /**
     * Вихід з системи: видаляє користувача з сесії.
     *
     * @param session HTTP-сесія користувача
     * @return редирект на головну сторінку
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("Logging out user");

        session.invalidate();
        return "redirect:/";
    }

}