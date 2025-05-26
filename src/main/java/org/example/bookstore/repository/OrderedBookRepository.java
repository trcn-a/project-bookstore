package org.example.bookstore.repository;

import org.example.bookstore.entity.OrderedBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторій для роботи з сутністю OrderedBook.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій.
 */
public interface OrderedBookRepository extends JpaRepository<OrderedBook, Long> {

    /**
     * Знаходить всі книги, які були замовлені в конкретному замовленні.
     *
     * @param id ідентифікатор замовлення
     * @return список книг, що входять до складу замовлення з вказаним id
     */
    List<OrderedBook> findByOrderId(Long id);
}
