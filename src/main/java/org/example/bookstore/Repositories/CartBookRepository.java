package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.CartBook;
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
     * Знаходить CartBook за ідентифікаторами кошика та книги.
     *
     * @param userId ідентифікатор кошика
     * @param bookId ідентифікатор книги
     * @return опціональний об'єкт CartBook, якщо такий є
     */
    Optional<CartBook> findByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Видаляє CartBook за ідентифікаторами кошика та книги.
     *
     * @param userId ідентифікатор кошика
     * @param bookId ідентифікатор книги
     */
    void deleteByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Знаходить усі CartBook для конкретного кошика.
     *
     * @param id ідентифікатор кошика
     * @return список CartBook для вказаного кошика
     */
    List<CartBook> findByUserIdOrderByIdAsc(Long id);

    /**
     * Обчислює загальну суму товарів у кошику за його ідентифікатором.
     *
     * @param userId ідентифікатор кошика
     * @return загальна сума товарів у кошику
     */
    @Query("SELECT SUM(cb.book.price * (1 - cb.book.discount / 100.0) * cb.quantity) FROM CartBook cb WHERE cb.user.id = :userId")
    Integer calculateTotalSumByUserId(@Param("userId") Long userId);

    @Query("SELECT cb.book.id FROM CartBook cb WHERE cb.user.id = :userId")
    List<Long> findBookIdsByUserId(Long userId);
}
