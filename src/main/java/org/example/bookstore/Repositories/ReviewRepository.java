package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.book.id = :bookId")
    double findAverageRatingByBookId(@Param("bookId") Long bookId);

    List<Review> findByBookId(Long bookId);

    Review findByBookIdAndUserId(Long bookId, Long userId);

    List<Review> findByUserId(Long userId);
}
