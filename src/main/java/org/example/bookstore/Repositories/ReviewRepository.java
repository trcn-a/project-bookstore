package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Репозиторій для роботи з сутністю Review.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Знаходить середній рейтинг книги за її ID.
     * Якщо рейтингів немає, повертає 0.
     *
     * @param bookId ID книги, для якої потрібно обчислити середній рейтинг.
     * @return Середній рейтинг книги.
     */
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.book.id = :bookId")
    double findAverageRatingByBookId(@Param("bookId") Long bookId);

    /**
     * Знаходить відгуки для книги за її ID.
     *
     * @param bookId ID книги, для якої потрібно знайти відгуки.
     * @return Список відгуків для книги.
     */
    List<Review> findByBookId(Long bookId);

    /**
     * Знаходить відгук для книги, написаний конкретним користувачем.
     *
     * @param bookId ID книги.
     * @param userId ID користувача.
     * @return Відгук для книги, написаний користувачем, або null, якщо відгук не знайдений.
     */
    Review findByBookIdAndUserId(Long bookId, Long userId);

    /**
     * Знаходить всі відгуки, написані користувачем.
     *
     * @param userId ID користувача.
     * @return Список відгуків користувача.
     */
    List<Review> findByUserId(Long userId);

    List<Review> findByBookTitleContainingIgnoreCase(String title);
}
