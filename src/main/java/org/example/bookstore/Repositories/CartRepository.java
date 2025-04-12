package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторій для роботи з сутністю Cart.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій з кошиками користувачів.
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Знаходить кошик користувача за його ідентифікатором.
     *
     * @param userId ідентифікатор користувача
     * @return опціональний об'єкт, що містить кошик користувача, якщо такий є
     */
    Optional<Cart> findByUserId(Long userId);
}
