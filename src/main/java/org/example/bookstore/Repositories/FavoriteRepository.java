
package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.Favorite;
import org.example.bookstore.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторій для роботи з сутністю Favorite.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій з обраними книгами користувачів.
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * Знаходить всі книги з обраних для конкретного користувача.
     *
     * @param user користувач, чиї обрані книги треба знайти
     * @return список усіх обраних книг для цього користувача
     */
    List<Favorite> findByUser(User user);

    /**
     * Знаходить обрану книгу конкретного користувача за книгою.
     *
     * @param user користувач
     * @param book книга
     * @return обрана книгу, якщо така є
     */
    Optional<Favorite> findByUserAndBook(User user, Book book);

    /**
     * Видаляє книгу з обраного для конкретного користувача.
     *
     * @param user користувач
     * @param book книга, яку треба видалити з обраного
     */
    void deleteByUserAndBook(User user, Book book);
}
