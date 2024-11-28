package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Реєстрація нового користувача
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String firstName,
                                             @RequestParam String lastName,
                                             @RequestParam String email,
                                             @RequestParam String password,
                                             @RequestParam String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Паролі не співпадають.");
        }
        User user = userService.registerUser(firstName, lastName, email, password);
        return ResponseEntity.ok(user);
    }

    // Авторизація користувача
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestParam String email,
                                          @RequestParam String password) {

        User user = userService.authenticateUser(email, password);
        return ResponseEntity.ok(user);
    }
}
