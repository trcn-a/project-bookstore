package org.example.bookstore.repository;

import org.example.bookstore.entity.Book;
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
            "    AND (:minPrice IS NULL OR (b.price - (b.price * b.discount / 100)) >= :minPrice)" +
            "    AND (:maxPrice IS NULL OR (b.price - (b.price * b.discount / 100)) <= :maxPrice)")
    Page<Book> filterBooks(@Param("authors") List<String> authors,
                           @Param("genres") List<String> genres,
                           @Param("publishers") List<String> publishers,
                           @Param("minPrice") Integer minPrice,
                           @Param("maxPrice") Integer maxPrice,
                           Pageable pageable);

    /**
     * Знаходить всі книги за ідентифікатором автора.
     *
     * @param authorId ідентифікатор автора
     * @return список книг цього автора
     */
    List<Book> findByAuthorId(Long authorId);

    @Query("SELECT b FROM Book b ORDER BY (b.price - (b.price * b.discount / 100))")
    Page<Book> findAllSortedByActualPrice(Pageable pageable);


    @Query("""
    SELECT b FROM Book b
    WHERE (:authors IS NULL OR b.author.name IN :authors)
    AND (:genres IS NULL OR b.genre.name IN :genres)
    AND (:publishers IS NULL OR b.publisher.name IN :publishers)
    AND (:minPrice IS NULL OR (b.price - (b.price * b.discount / 100)) >= :minPrice)
    AND (:maxPrice IS NULL OR (b.price - (b.price * b.discount / 100)) <= :maxPrice)
    ORDER BY (b.price - (b.price * b.discount / 100)) ASC
    """)
    Page<Book> filterAndSortByActualPriceAsc(@Param("authors") List<String> authors,
                                             @Param("genres") List<String> genres,
                                             @Param("publishers") List<String> publishers,
                                             @Param("minPrice") Integer minPrice,
                                             @Param("maxPrice") Integer maxPrice,
                                             Pageable pageable);

    @Query("""
    SELECT b FROM Book b
    WHERE (:authors IS NULL OR b.author.name IN :authors)
    AND (:genres IS NULL OR b.genre.name IN :genres)
    AND (:publishers IS NULL OR b.publisher.name IN :publishers)
    AND (:minPrice IS NULL OR (b.price - (b.price * b.discount / 100)) >= :minPrice)
    AND (:maxPrice IS NULL OR (b.price - (b.price * b.discount / 100)) <= :maxPrice)
    ORDER BY (b.price - (b.price * b.discount / 100)) DESC
    """)
    Page<Book> filterAndSortByActualPriceDesc(@Param("authors") List<String> authors,
                                              @Param("genres") List<String> genres,
                                              @Param("publishers") List<String> publishers,
                                              @Param("minPrice") Integer minPrice,
                                              @Param("maxPrice") Integer maxPrice,
                                              Pageable pageable);

    @Query("""
    SELECT b FROM Book b
    WHERE (:authors IS NULL OR b.author.name IN :authors)
    AND (:genres IS NULL OR b.genre.name IN :genres)
    AND (:publishers IS NULL OR b.publisher.name IN :publishers)
    AND (:minPrice IS NULL OR (b.price - (b.price * b.discount / 100)) >= :minPrice)
    AND (:maxPrice IS NULL OR (b.price - (b.price * b.discount / 100)) <= :maxPrice)
    ORDER BY (b.price - (b.price * b.discount / 100))
    """)
    Page<Book> filterAndSortByActualPrice(@Param("authors") List<String> authors,
                                          @Param("genres") List<String> genres,
                                          @Param("publishers") List<String> publishers,
                                          @Param("minPrice") Integer minPrice,
                                          @Param("maxPrice") Integer maxPrice,
                                          Pageable pageable);

}
