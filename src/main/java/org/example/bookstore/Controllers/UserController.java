package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.bookstore.Entities.Order;
import org.example.bookstore.Entities.OrderedBook;
import org.example.bookstore.Entities.User;
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
import java.util.stream.Collectors;




/**
 * Контролер для обробки запитів, пов'язаних з профілем користувача.
 * Відповідає за реєстрацію, вхід, перегляд та редагування  особистих даних,
 * перегляд замовлень і відгуків користувача.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final OrderService orderService;

    /**
     * Конструктор контролера, що інʼєктує сервіси для роботи з користувачами, відгуками та замовленнями.
     *
     * @param userService сервіс для роботи з користувачами
     * @param reviewService сервіс для роботи з відгуками
     * @param orderService сервіс для роботи з замовленнями
     */
    @Autowired
    public UserController(UserService userService, ReviewService reviewService, OrderService orderService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.orderService = orderService;
    }

    /**
     * Показує форму для реєстрації нового користувача.
     *
     * @return назва HTML-шаблону "register"
     */
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    /**
     * Показує форму для входу користувача.
     *
     * @return назва HTML-шаблону "login"
     */
    @GetMapping("/login")
    public String showLoginForm() {
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

        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("confirmPassword", confirmPassword);

        // Перевірка валідності даних
        if (firstName == null || firstName.isBlank()) {
            model.addAttribute("error", "First name is required");
            return "register";
        }
        if (lastName == null || lastName.isBlank()) {
            model.addAttribute("error", "Last name is required");
            return "register";
        }
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "Email should be valid");
            return "register";
        }
        if (password == null || password.length() < 8 ||
                !password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*")) {
            model.addAttribute("error",
                    "Password must be at least 8 characters long, including at " +
                            "least one uppercase letter, one lowercase letter, one digit, and one special character");
            return "register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Password and password confirmation do not match");
            return "register";
        }

        try {
            User user = userService.registerUser(firstName, lastName, email, password);
            session.setAttribute("user", user);

            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());

            return "register";
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
        model.addAttribute("email", email);
        model.addAttribute("password", password);

        // Перевірка валідності email
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "Email should be valid");
            return "login";
        }

        try {
            User user = userService.authenticateUser(email, password);
            session.setAttribute("user", user);
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
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
        session.invalidate();
        return "redirect:/";
    }

    /**
     * Показує профіль користувача. Якщо користувач не авторизований, перенаправляє на сторінку входу.
     *
     * @param user обʼєкт користувача, збережений в сесії
     * @return сторінка профілю або редирект на сторінку входу
     */
    @GetMapping("/profile")
    public String showProfile(@SessionAttribute(value = "user", required = false) User user) {
        if (user == null) {
            return "redirect:/user/login";
        }
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
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String phoneNumber,
            @SessionAttribute("user") User user,
            Model model) {
        try {
            User updatedUser = userService.updateUserProfile(user.getId(), firstName, lastName, phoneNumber, user);
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            model.addAttribute("profileUpdateSuccess", true);
        } catch (IllegalArgumentException e) {
            model.addAttribute("profileUpdateError", e.getMessage());
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
    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            @SessionAttribute("user") User user,
            Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("passwordUpdateError", "Новий пароль та підтвердження не співпадають");
            return "profile";
        }

        if (newPassword.length() < 8 || !newPassword.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*")) {
            model.addAttribute("passwordUpdateError", "Пароль повинен містити мінімум 8 " +
                    "символів, включаючи великі та малі літери, цифри та спеціальні символи");
            return "profile";
        }

        try {
            userService.changePassword(user.getId(), currentPassword, newPassword);
            model.addAttribute("passwordUpdateSuccess", true);
        } catch (IllegalArgumentException e) {
            model.addAttribute("passwordUpdateError", e.getMessage());
        }
        return "profile";
    }

    /**
     * Показує відгуки користувача.
     *
     * @param user обʼєкт користувача, збережений в сесії
     * @param model модель для передачі даних у шаблон
     * @return сторінка з відгуками користувача або редирект на сторінку входу
     */
    @GetMapping("/reviews")
    public String showUserReviews(@SessionAttribute(value = "user", required = false) User user, Model model) {
        try {
            model.addAttribute("reviews", reviewService.getUserReviews(user.getId()));
            return "user-reviews";
        } catch (IllegalStateException e) {
            return "redirect:/user/login";
        }
    }

    /**
     * Показує історію замовлень користувача.
     *
     * @param user обʼєкт користувача, збережений в сесії
     * @param model модель для передачі даних у шаблон
     * @return сторінка з історією замовлень користувача або редирект на сторінку входу
     */
    @GetMapping("/orders")
    public String showOrderHistory(@SessionAttribute(value = "user", required = false) User user, Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        List<Order> orders = orderService.getUserOrders(user.getId());

        model.addAttribute("orders", orders);
        Map<Order, List<OrderedBook>> ordersWithBooks = orders.stream()
                .collect(Collectors.toMap(order -> order, order -> orderService.getOrderedBooks(order.getId())));

        model.addAttribute("ordersWithBooks", ordersWithBooks);
        return "order-history";
    }

    /**
     * Скасовує замовлення користувача.
     *
     * @param orderId ідентифікатор замовлення
     * @param session HTTP-сесія користувача
     * @param redirectAttributes атрибути для редиректу з повідомленням
     * @return редирект на сторінку історії замовлень
     */
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            orderService.cancelOrder(orderId, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Замовлення успішно скасовано");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/orders";
    }
}