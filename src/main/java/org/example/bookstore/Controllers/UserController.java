package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Entities.Order;
import org.example.bookstore.Entities.OrderedBook;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.CartService;
import org.example.bookstore.Services.ReviewService;
import org.example.bookstore.Services.UserService;
import org.example.bookstore.Services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.PostMapping;


import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Контролер для обробки запитів, пов'язаних з профілем користувача.
 * Відповідає за реєстрацію, вхід, перегляд та редагування  особистих даних,
 * перегляд замовлень і відгуків користувача.
 */
@Controller
@RequestMapping("/profile")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ReviewService reviewService;
    private final OrderService orderService;
    private final CartService cartService;

    /**
     * Конструктор контролера, що інʼєктує сервіси для роботи з користувачами, відгуками та замовленнями.
     *
     * @param userService сервіс для роботи з користувачами
     * @param reviewService сервіс для роботи з відгуками
     * @param orderService сервіс для роботи з замовленнями
     */
    @Autowired
    public UserController(UserService userService, ReviewService reviewService, OrderService orderService, CartService cartService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.orderService = orderService;
        this.cartService = cartService;
    }

    /**
     * Показує профіль користувача. Якщо користувач не авторизований, перенаправляє на сторінку входу.
     *
     * @param user обʼєкт користувача, збережений в сесії
     * @return сторінка профілю або редирект на сторінку входу
     */
    @GetMapping("")
    public String showProfile(@SessionAttribute(value = "user", required = false) User user) {
        if (user == null) {
            logger.warn("User not authenticated, redirecting to login");

            return "redirect:/user/login";
        }
        logger.info("Displaying profile for user: {}", user.getEmail());

        return "profile";
    }

    /**
     * Оновлює профіль користувача: імʼя, прізвище та номер телефону.
     *
     * @param firstName імʼя користувача
     * @param lastName прізвище користувача
     * @param phoneNumber номер телефону користувача
     * @param user обʼєкт користувача, збережений в сесії
     * @param model модель для передачі даних у шаблон
     * @return сторінка профілю з повідомленням про успіх або помилку
     */
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNumber,
            @SessionAttribute("user") User user,
            Model model) {
        try {
            logger.info("Updating profile for user: {}", user.getEmail());

            User updatedUser = userService.updateUserProfile(user.getId(), firstName, lastName, phoneNumber, user);
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            model.addAttribute("profileUpdateSuccess", true);
            logger.info("Profile updated successfully for user: {}", user.getEmail());

        } catch (IllegalArgumentException e) {
            UUID errorId = UUID.randomUUID();
            model.addAttribute("profileUpdateError", e.getMessage());
            logger.error("Error ID: {} - Profile update failed for user: {}: {}",
                    errorId, user.getEmail(), e.getMessage());
        }
        return "profile";
    }

    /**
     * Змінює пароль користувача.
     *
     * @param currentPassword поточний пароль користувача
     * @param newPassword новий пароль користувача
     * @param confirmPassword підтвердження нового пароля
     * @param user обʼєкт користувача, збережений в сесії
     * @param model модель для передачі даних у шаблон
     * @return сторінка профілю з повідомленням про успіх або помилку
     */
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            @SessionAttribute("user") User user,
            Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("passwordUpdateError",
                    "Новий пароль та підтвердження не співпадають");
            logger.warn("Password change failed: New password and confirmation do not match for user: {}",
                    user.getEmail());

            return "profile";
        }

        if (newPassword.length() < 8 || !newPassword.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*")) {
            model.addAttribute("passwordUpdateError", "Пароль повинен містити мінімум 8 " +
                    "символів, включаючи великі та малі літери, цифри та спеціальні символи");
            logger.warn("Password change failed: Password does not meet criteria for user: {}", user.getEmail());

            return "profile";
        }

        try {
            userService.changePassword(user.getId(), currentPassword, newPassword);
            model.addAttribute("passwordUpdateSuccess", true);
            logger.info("Password successfully changed for user: {}", user.getEmail());

        } catch (IllegalArgumentException e) {
            UUID errorId = UUID.randomUUID();
            model.addAttribute("passwordUpdateError", e.getMessage());
            logger.error("Error ID: {} - Password change failed for user: {}: {}",
                    errorId, user.getEmail(), e.getMessage());
        }
        return "profile";
    }




}