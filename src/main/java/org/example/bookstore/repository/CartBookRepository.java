package org.example.bookstore.repository;

import org.example.bookstore.entity.CartBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторій для роботи з сутністю CartBook.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій з товарами в кошику користувача.
 */
public interface CartBookRepository extends JpaRepository<CartBook, Long> {

    /**
     * Знаходить CartBook за ідентифікаторами користувача та книги.
     *
     * @param userId ідентифікатор користувача
     * @param bookId ідентифікатор книги
     * @return опціональний об'єкт CartBook, якщо такий є
     */
    Optional<CartBook> findByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Видаляє CartBook за ідентифікаторами користувача та книги.
     *
     * @param userId ідентифікатор користувача
     * @param bookId ідентифікатор книги
     */
    void deleteByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Знаходить усі CartBook для конкретного користувача.
     *
     * @param id ідентифікатор користувача
     * @return список CartBook для вказаного користувача
     */
    List<CartBook> findByUserIdOrderByIdAsc(Long id);

    /**
     * Обчислює загальну суму товарів у кошику за ідентифікатором користувача.
     *
     * @param userId ідентифікатор користувача
     * @return загальна сума товарів у кошику
     */
    @Query("SELECT SUM(cb.book.price * (1 - cb.book.discount / 100.0) * cb.quantity) FROM CartBook cb WHERE cb.user.id = :userId")
    Integer calculateTotalSumByUserId(@Param("userId") Long userId);

    @Query("SELECT cb.book.id FROM CartBook cb WHERE cb.user.id = :userId")
    List<Long> findBookIdsByUserId(Long userId);
}
