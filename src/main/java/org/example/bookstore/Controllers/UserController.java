package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // Реєстрація нового користувача
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

        if (firstName == null || firstName.isBlank()) {
            model.addAttribute("error", "First name is required");
            return "register"; // Повертаємо на сторінку реєстрації з повідомленням
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
            model.addAttribute("error", "Password must be at least 8 characters long, including at least one uppercase letter, one lowercase letter, one digit, and one special character");
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
            // Якщо виникла помилка (наприклад, email вже зареєстрований), передаємо її на сторінку
            model.addAttribute("error", ex.getMessage());  // Додаємо помилку в модель

            return "register";  // Повертаємо на сторінку реєстрації
        }
    }


    // Авторизація користувача
    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {

        model.addAttribute("email", email);
        model.addAttribute("password", password);

        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "Email should be valid");

            return "login";
        }

        try {
            User user = userService.authenticateUser(email, password);
            session.setAttribute("user", user); // зберігаємо користувача в сесії після авторизації
            return "redirect:/";  // Переходимо на головну сторінку після успішної авторизації
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());  // Додаємо помилку в модель

            return "login";
        }
    }

    // Вихід з системи
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // очищаємо сесію
        return "redirect:/";  // Переходимо на сторінку входу
    }
}

//@Controller
//@RequestMapping("/user")
//public class UserController {
//
//    private final UserService userService;
//
//    @Autowired
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping("/register")
//    public String showRegistrationForm() {
//        return "register";  // Це ім'я HTML-файлу для реєстрації
//    }
//
//    // Відображення сторінки авторизації
//    @GetMapping("/login")
//    public String showLoginForm() {
//        return "login";  // Це ім'я HTML-файлу для авторизації
//    }
//    // Реєстрація нового користувача
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(
//            @RequestParam String firstName,
//            @RequestParam String lastName,
//            @RequestParam String email,
//            @RequestParam String password,
//            @RequestParam String confirmPassword
//    ) {
//        if (firstName == null || firstName.isBlank()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("First name is required");
//        }
//        if (lastName == null || lastName.isBlank()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Last name is required");
//        }
//        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email should be valid");
//        }
//        if (password == null || password.length() < 8 ||
//                !password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*")) {
//           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 8 characters long, including at least one uppercase letter, one lowercase letter, one digit, and one special character");
//
//        }
//        if (!password.equals(confirmPassword)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password and password confirmation do not match");
//        }
//
//        try {
//            User user = userService.registerUser(firstName, lastName, email, password);
//            return ResponseEntity.ok(user);
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//        }
//    }
//
//
//    // Авторизація користувача
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(
//            @RequestParam String email,
//            @RequestParam String password) {
//
//        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email should be valid");
//        }
//
//        try {
//            User user = userService.authenticateUser(email, password);
//            return ResponseEntity.ok(user);
//        } catch (IllegalArgumentException ex) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
//        }
//    }
//
//
//
//}
