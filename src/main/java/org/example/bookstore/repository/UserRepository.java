package org.example.bookstore.repository;

import org.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторій для роботи з сутністю User.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Знаходить користувача за його email.
     *
     * @param email Email користувача.
     * @return Користувач з відповідним email, або null, якщо користувач не знайдений.
     */
    Optional<User> findByEmail(String email);
}
