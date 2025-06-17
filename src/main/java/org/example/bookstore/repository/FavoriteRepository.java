
package org.example.bookstore.repository;

import org.example.bookstore.entity.Book;
import org.example.bookstore.entity.Favorite;
import org.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
     * Знаходить обрану книгу конкретного користувача за книгою.
     *
     * @param user користувач
     * @param book книга
     * @return обрана книгу, якщо така є
     */
    Optional<Favorite> findByUserAndBook(User user, Book book);


    @Query("SELECT f.book FROM Favorite f WHERE f.user = :user")
    List<Book> findBooksByUser(User user);

    @Query("SELECT f.book.id FROM Favorite f WHERE f.user = :user")
    List<Long> findBookIdsByUser(User user);


    /**
     * Видаляє книгу з обраного для конкретного користувача.
     *
     * @param user користувач
     * @param book книга, яку треба видалити з обраного
     */
    void deleteByUserAndBook(User user, Book book);
}
