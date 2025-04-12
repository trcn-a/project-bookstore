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
     * @param cartId ідентифікатор кошика
     * @param bookId ідентифікатор книги
     * @return опціональний об'єкт CartBook, якщо такий є
     */
    Optional<CartBook> findByCartIdAndBookId(Long cartId, Long bookId);

    /**
     * Видаляє CartBook за ідентифікаторами кошика та книги.
     *
     * @param cartId ідентифікатор кошика
     * @param bookId ідентифікатор книги
     */
    void deleteByCartIdAndBookId(Long cartId, Long bookId);

    /**
     * Знаходить усі CartBook для конкретного кошика.
     *
     * @param id ідентифікатор кошика
     * @return список CartBook для вказаного кошика
     */
    List<CartBook> findByCartId(Long id);

    /**
     * Обчислює загальну суму товарів у кошику за його ідентифікатором.
     *
     * @param cartId ідентифікатор кошика
     * @return загальна сума товарів у кошику
     */
    @Query("SELECT SUM(cb.book.actualPrice * cb.quantity) FROM CartBook cb WHERE cb.cart.id = :cartId")
    Integer calculateTotalSumByCartId(@Param("cartId") Long cartId);
}
