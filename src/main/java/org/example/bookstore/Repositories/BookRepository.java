package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Репозиторій для роботи з сутністю Book.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій з книгами.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Знаходить всі книги з використанням пагінації.
     *
     * @param pageable інформація про пагінацію
     * @return сторінка книг
     */
    Page<Book> findAll(Pageable pageable);

    /**
     * Шукає книги за заголовком або автором.
     * Пошук нечутливий до регістру.
     *
     * @param searchQuery запит для пошуку
     * @return список книг, що відповідають критеріям пошуку
     */
    @Query("SELECT b FROM Book b " +
            "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
            "OR LOWER(b.author.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) ")
    List<Book> searchBooks(@Param("searchQuery") String searchQuery);

    /**
     * Фільтрує книги за автором, жанром, видавцем, мінімальною та максимальною ціною.
     * Якщо параметри не задані (NULL), вони не враховуються у фільтрації.
     *
     * @param authors   список авторів
     * @param genres    список жанрів
     * @param publishers список видавців
     * @param minPrice мінімальна ціна
     * @param maxPrice максимальна ціна
     * @return список книг, що відповідають критеріям фільтрації
     */
    @Query("SELECT b FROM Book b " +
            "WHERE (:authors IS NULL OR b.author.name IN :authors) " +
            "AND (:genres IS NULL OR b.genre.name IN :genres) " +
            "AND (:publishers IS NULL OR b.publisher.name IN :publishers) " +
            "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR b.price <= :maxPrice)")
    List<Book> filterBooks(List<String> authors, List<String> genres, List<String> publishers,
                           Integer minPrice, Integer maxPrice);

    /**
     * Знаходить всі книги за ідентифікатором автора.
     *
     * @param authorId ідентифікатор автора
     * @return список книг цього автора
     */
    List<Book> findByAuthorId(Long authorId);
}
