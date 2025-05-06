package org.example.bookstore.Controllers;

import org.example.bookstore.Config.CustomUserDetails;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Показує профіль користувача. Якщо користувач не авторизований, перенаправляє на сторінку входу.
     *
     * @param currentUser обʼєкт користувача, отриманий через @AuthenticationPrincipal
     * @return сторінка профілю або редирект на сторінку входу
     */
    @GetMapping("")
    public String showProfile(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        if (currentUser != null) {
            logger.info("Displaying profile for user: {}", currentUser.getUsername());
            model.addAttribute("user", currentUser.getUser());
        }
        return "profile";
    }

    /**
     * Оновлює профіль користувача: імʼя, прізвище та номер телефону.
     *
     * @param firstName імʼя користувача
     * @param lastName прізвище користувача
     * @param phoneNumber номер телефону користувача
     * @param currentUser обʼєкт користувача, отриманий через @AuthenticationPrincipal
     * @param model модель для передачі даних у шаблон
     * @return сторінка профілю з повідомленням про успіх або помилку
     */
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNumber,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Model model) {
        try {
            logger.info("Updating profile for user: {}", currentUser.getUsername());

            User updatedUser = userService.updateUserProfile(currentUser.getUser().getId(), firstName, lastName, phoneNumber);
            model.addAttribute("user", updatedUser);

            model.addAttribute("profileUpdateSuccess", true);
            logger.info("Profile updated successfully for user: {}", currentUser.getUsername());

        } catch (IllegalArgumentException e) {
            UUID errorId = UUID.randomUUID();
            model.addAttribute("profileUpdateError", e.getMessage());
            logger.error("Error ID: {} - Profile update failed for user: {}: {}",
                    errorId, currentUser.getUsername(), e.getMessage());
        }
        return "profile";
    }

    /**
     * Змінює пароль користувача.
     *
     * @param currentPassword поточний пароль користувача
     * @param newPassword новий пароль користувача
     * @param confirmPassword підтвердження нового пароля
     * @param currentUser обʼєкт користувача, отриманий через @AuthenticationPrincipal
     * @param model модель для передачі даних у шаблон
     * @return сторінка профілю з повідомленням про успіх або помилку
     */
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("passwordUpdateError",
                    "Новий пароль та підтвердження не співпадають");
            logger.warn("Password change failed: New password and confirmation do not match for user: {}",
                    currentUser.getUsername());
            return "profile";
        }

        if (newPassword.length() < 8 || !newPassword.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*")) {
            model.addAttribute("passwordUpdateError", "Пароль повинен містити мінімум 8 " +
                    "символів, включаючи великі та малі літери, цифри та спеціальні символи");
            logger.warn("Password change failed: Password does not meet criteria for user: {}",
                    currentUser.getUsername());
            return "profile";
        }
        model.addAttribute("user", currentUser.getUser());

        try {
            userService.changePassword(currentUser.getUser().getId(), currentPassword, newPassword);
            model.addAttribute("passwordUpdateSuccess", true);
            logger.info("Password successfully changed for user: {}", currentUser.getUsername());

        } catch (IllegalArgumentException e) {

            UUID errorId = UUID.randomUUID();
            model.addAttribute("passwordUpdateError", e.getMessage());
            logger.error("Error ID: {} - Password change failed for user: {}: {}",
                    errorId, currentUser.getUsername(), e.getMessage());
        }
        return "profile";
    }

}
