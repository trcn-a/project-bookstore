package org.example.bookstore.repository;

import org.example.bookstore.Entities.OrderedBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderedBookRepository extends JpaRepository<OrderedBook, Long> {
}
