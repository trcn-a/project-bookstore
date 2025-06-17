package org.example.bookstore.controller;

import org.example.bookstore.config.CustomUserDetails;
import org.example.bookstore.entity.User;
import org.example.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("")
    public String showProfile(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        if (currentUser != null) {
            model.addAttribute("activePage", "profile");

            model.addAttribute("user", currentUser.getUser());
        }
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNumber,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            logger.info("Updating profile for user: {}", currentUser.getUsername());

            User updatedUser = userService.updateUserProfile(currentUser.getUser().getId(), firstName, lastName, phoneNumber);

            CustomUserDetails updatedUserDetails = new CustomUserDetails(updatedUser);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    updatedUserDetails,
                    currentUser.getPassword(),
                    updatedUserDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            redirectAttributes.addFlashAttribute("profileUpdateSuccess", true);

        } catch (IllegalArgumentException e) {
            UUID errorId = UUID.randomUUID();
            redirectAttributes.addFlashAttribute("profileUpdateError", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordUpdateError",
                    "Новий пароль та підтвердження не співпадають");
            return "redirect:/profile";
        }

        if (newPassword.length() < 8 || !newPassword.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*")) {
            redirectAttributes.addFlashAttribute("passwordUpdateError", "Пароль має містити мінімум 8 " +
                    "символів, великі та малі літери, цифри та спеціальні символи");
            return "redirect:/profile";
        }
        model.addAttribute("user", currentUser.getUser());

        try {
            userService.changePassword(currentUser.getUser().getId(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("passwordUpdateSuccess", true);

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("passwordUpdateError",
                    e.getMessage());

        }
        return "redirect:/profile";
    }
}
