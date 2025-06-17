package org.example.bookstore.repository;

import org.example.bookstore.entity.Order;
import org.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторій для роботи з сутністю Order.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {



    /**
     * Знаходить всі замовлення для конкретного користувача, відсортовані за датою створення у зворотньому порядку.
     *
     * @param userId ідентифікатор користувача
     * @return список замовлень користувача, відсортованих за датою створення (від найновіших)
     */
    List<Order> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<Order> findAllByOrderByUpdatedAtDesc();

}
