package org.example.bookstore.repository;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook(Book book);
}
