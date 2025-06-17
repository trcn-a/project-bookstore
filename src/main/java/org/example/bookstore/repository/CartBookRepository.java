package org.example.bookstore.repository;

import org.example.bookstore.entity.CartBook;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторій для роботи з CartBook
 */
public interface CartBookRepository extends JpaRepository<CartBook, Long> {

    /**
     * Знаходить CartBook за ідентифікатором книги та користувача
     *
     * @param userId ідентифікатор користувача
     * @param bookId ідентифікатор книги
     * @return об'єкт CartBook
     */
    Optional<CartBook> findByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Видаляє CartBook за ідентифікатором книги та користувача
     *
     * @param userId ідентифікатор користувача
     * @param bookId ідентифікатор книги
     */
    void deleteByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Знаходить усі книги в кошику користувача за його ідентифікатором
     *
     * @param userId ідентифікатор користувача
     * @return список доданих книг CartBook
     */
    List<CartBook> findByUserIdOrderByIdAsc(Long userId);

    /**
     * Повертає список ідентифікаторів книг, що знаходяться в кошику
     *
     * @param userId ідентифікатор користувача
     * @return список ідентифікаторів доданих книг
     */
    @Query("SELECT cb.book.id FROM CartBook cb WHERE cb.user.id = :userId")
    List<Long> findBookIdsByUserId(Long userId);

    /**
     * Обчислює загальну суму кошика користувача
     *
     * @param userId ідентифікатор користувача
     * @return загальна сума кошика
     */
    @Query("SELECT SUM(cb.book.price * (1 - cb.book.discount / 100.0) * cb.quantity) FROM CartBook cb WHERE cb.user.id = :userId")
    Integer calculateTotalSumByUserId( Long userId);

    /**
     * Обчислює кількість книг в кошику користувача
     *
     * @param userId ідентифікатор користувача
     * @return загальна кількість книг
     */
    @Query("SELECT COALESCE(SUM(cb.quantity), 0) FROM CartBook cb WHERE cb.user.id = :userId")
    Integer calculateCartQuantity(Long userId);

}
