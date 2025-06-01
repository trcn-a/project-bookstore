package org.example.bookstore.repository;

import org.example.bookstore.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторій для роботи з сутністю Author.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій з авторами.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByName(String name);

    @Query("SELECT a.name FROM Author a")
    List<String> findAllAuthorNames();


    @Query("""
        SELECT AVG(r.rating)
        FROM Author a
        JOIN Book b ON b.author.id = a.id
        JOIN Review r ON r.book.id = b.id
        WHERE a.id = :authorId
    """)
    Optional<Double> findAverageRatingByAuthorId(Long authorId);

    @Query("""
        SELECT COUNT(r.id)
        FROM Author a
        JOIN Book b ON b.author.id = a.id
        JOIN Review r ON r.book.id = b.id
        WHERE a.id = :authorId
    """)
    Optional<Integer> findReviewCountByAuthorId(Long authorId);
}


