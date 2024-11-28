package org.example.bookstore.repository;


import org.example.bookstore.Entities.Cart;
import org.example.bookstore.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}

