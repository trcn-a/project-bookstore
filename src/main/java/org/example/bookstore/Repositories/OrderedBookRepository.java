package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.OrderedBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderedBookRepository extends JpaRepository<OrderedBook, Long> {
    List<OrderedBook> findByOrderId(Long id);

}
