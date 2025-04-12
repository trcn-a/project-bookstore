package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Order;
import org.example.bookstore.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторій для роботи з сутністю Order.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Знаходить всі замовлення для конкретного користувача.
     *
     * @param user користувач, для якого шукаються замовлення
     * @return список замовлень, що належать вказаному користувачу
     */
    List<Order> findByUser(User user);

    /**
     * Знаходить всі замовлення для конкретного користувача, відсортовані за датою створення у зворотньому порядку.
     *
     * @param userId ідентифікатор користувача
     * @return список замовлень користувача, відсортованих за датою створення (від найновіших)
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
