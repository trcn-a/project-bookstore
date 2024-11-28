package org.example.bookstore.repository;

import org.example.bookstore.Entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);

    @Query("SELECT b FROM Book b " +
            "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
            "OR LOWER(b.author.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    List<Book> searchBooks(String searchQuery);
    @Query("SELECT b FROM Book b " +
            "WHERE (:authors IS NULL OR b.author.name IN :authors) " +
            "AND (:genres IS NULL OR b.genre.name IN :genres) " +
            "AND (:publishers IS NULL OR b.publisher.name IN :publishers) " +
            "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR b.price <= :maxPrice)")
    List<Book> filterBooks(List<String> authors, List<String> genres, List<String> publishers, Integer minPrice, Integer maxPrice);
}