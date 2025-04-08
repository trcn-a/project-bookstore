package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Order;
import org.example.bookstore.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
