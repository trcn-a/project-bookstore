package org.example.bookstore.Services;

import org.example.bookstore.Entities.User;
import org.example.bookstore.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        if (userRepository.findByEmail(email) != null) {
            logger.error("Email {} is already in use", email);
            throw new IllegalArgumentException("Email already in use.");
        }

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
     * Авторизує користувача за електронною поштою та паролем.
     *
     * @param email        Електронна пошта користувача.
     * @param rawPassword  Пароль користувача.
     * @return Користувач, якщо авторизація пройшла успішно.
     * @throws IllegalArgumentException Якщо email не знайдено або пароль неправильний.
     */
    public User authenticateUser(String email, String rawPassword) {
        logger.info("Authenticating user with email={}", email);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            logger.error("User with email={} not found", email);
            throw new IllegalArgumentException("Email not found");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            logger.error("Incorrect password for user with email={}", email);
            throw new IllegalArgumentException("Invalid password");
        }

        logger.info("User with email={} authenticated successfully", email);
        return user;
    }

    /**
     * Отримує користувача за його електронною поштою.
     *
     * @param email Електронна пошта користувача.
     * @return Користувач або null, якщо користувач не знайдений.
     */
    public User getUserByEmail(String email) {
        logger.info("Fetching user with email={}", email);
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
        logger.info("Updating profile for user with id={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with id={} not found", userId);
                    return new IllegalArgumentException("User not found");
                });

        if (phoneNumber == null || phoneNumber.isBlank()) {
            logger.error("Phone number cannot be empty");
            throw new IllegalArgumentException("Phone number is required");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user = userRepository.save(user);

        logger.info("User profile with id={} updated", userId);
        return user;
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
        logger.info("Changing password for user with id={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with id={} not found", userId);
                    return new IllegalArgumentException("User not found");
                });

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.error("Incorrect current password for user with id={}", userId);
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password for user with id={} changed successfully", userId);
        return user;
    }
}
