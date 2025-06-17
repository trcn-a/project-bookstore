package org.example.bookstore.service;

import org.example.bookstore.entity.User;
import org.example.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Сервісний клас для управління користувачами в системі.
 * Включає методи для реєстрації, авторизації, оновлення профілю користувача,
 * зміни пароля та отримання користувача за електронною поштою.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Конструктор, який ініціалізує UserService з вказаним репозиторієм користувачів
     * та створює об'єкт шифрування паролів за допомогою BCrypt.
     *
     * @param userRepository Репозиторій для взаємодії з даними користувачів у базі даних.
     */
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
        logger.info("Registering user with email={}", email);



        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Прізвище є обов’язковим.");
        }
        if (!lastName.matches("^[А-Яа-яЇїІіЄєA-Za-z\\-']{2,30}$")) {
            throw new IllegalArgumentException("Прізвище має містити лише літери та бути довжиною від 2 до 30 символів.");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("Ім’я є обов’язковим.");
        }
        if (!firstName.matches("^[А-Яа-яЇїІіЄєA-Za-z\\-']{2,30}$")) {
            throw new IllegalArgumentException("Ім’я має містити лише літери та бути довжиною від 2 до 30 символів.");
        }
        if (email == null || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Невірний формат електронної пошти.");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Пароль є обов’язковим.");
        }
        if (rawPassword.length() < 8 || !rawPassword.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*")) {
            throw new IllegalArgumentException("Пароль має містити щонайменше 8 символів, включаючи великі та малі літери, цифру і спецсимвол.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Ця електронна пошта вже використовується.");
        }

        // Створення та збереження користувача
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");

        user = userRepository.save(user);
        logger.info("User with email={} registered successfully", email);

        return user;
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
                                  String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено."));

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("Ім’я є обов’язковим.");
        }
        if (!firstName.matches("^[А-Яа-яЇїІіЄєA-Za-z\\-']{2,30}$")) {
            throw new IllegalArgumentException("Ім’я має містити лише літери та бути довжиною від 2 до 30 символів.");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Прізвище є обов’язковим.");
        }
        if (!lastName.matches("^[А-Яа-яЇїІіЄєA-Za-z\\-']{2,30}$")) {
            throw new IllegalArgumentException("Прізвище має містити лише літери та бути довжиною від 2 до 30 символів.");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Номер телефону є обов’язковим.");
        }
        if (!phoneNumber.matches("^\\+?[0-9\\-\\s]{7,15}$")) {
            throw new IllegalArgumentException("Невірний формат номеру телефону.");
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
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено."));

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("Поточний пароль є обов’язковим.");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Неправильний поточний пароль.");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Новий пароль є обов’язковим.");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Новий пароль не повинен збігатися з поточним.");
        }

        if (newPassword.length() < 8 || !newPassword.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*")) {
            throw new IllegalArgumentException("Новий пароль має містити щонайменше 8 символів, включаючи великі та малі літери, цифру і спецсимвол.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);

    }
}
