package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

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
    User findByEmail(String email);
}
