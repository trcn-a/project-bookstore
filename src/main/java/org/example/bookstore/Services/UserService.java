package org.example.bookstore.Services;

import org.example.bookstore.Entities.User;
import org.example.bookstore.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * Сервісний клас для управління користувачами в системі.
 * Включає методи для реєстрації, авторизації, оновлення профілю користувача,
 * зміни пароля та отримання користувача за електронною поштою.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Реєструє нового користувача.
     *
     * @param firstName    Ім'я користувача.
     * @param lastName     Прізвище користувача.
     * @param email        Електронна пошта користувача.
     * @param rawPassword  Пароль користувача.
     * @return Створений користувач.
     * @throws IllegalArgumentException Якщо email вже використовується.
     */
    public User registerUser(String firstName, String lastName, String email, String rawPassword) {

        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");

        return userRepository.save(user);
    }

    /**
     * Авторизує користувача за електронною поштою та паролем.
     *
     * @param email        Електронна пошта користувача.
     * @param rawPassword  Пароль користувача.
     * @return Користувач, якщо авторизація пройшла успішно.
     * @throws IllegalArgumentException Якщо email не знайдено або пароль неправильний.
     */
    public User authenticateUser(String email, String rawPassword) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("Email not found");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        return user;
    }

    /**
     * Отримує користувача за його електронною поштою.
     *
     * @param email Електронна пошта користувача.
     * @return Користувач або null, якщо користувач не знайдений.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Оновлює профіль користувача.
     *
     * @param userId        Ідентифікатор користувача.
     * @param firstName     Ім'я користувача.
     * @param lastName      Прізвище користувача.
     * @param phoneNumber   Номер телефону користувача.
     * @param currentUser   Поточний користувач.
     * @return Оновлений користувач.
     * @throws IllegalArgumentException Якщо користувач не знайдений або номер телефону порожній.
     */
    public User updateUserProfile(Long userId, String firstName, String lastName,
                                  String phoneNumber, User currentUser) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        return userRepository.save(user);
    }

    /**
     * Змінює пароль користувача.
     *
     * @param userId         Ідентифікатор користувача.
     * @param currentPassword Поточний пароль користувача.
     * @param newPassword     Новий пароль користувача.
     * @return Оновлений користувач.
     * @throws IllegalArgumentException Якщо поточний пароль неправильний або користувач не знайдений.
     */
    public User changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return user;
    }
}
