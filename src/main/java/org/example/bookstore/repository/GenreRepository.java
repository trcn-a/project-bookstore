package org.example.bookstore.repository;

import org.example.bookstore.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторій для роботи з сутністю Genre.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій.
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByName(String name);

    @Query("SELECT g.name FROM Genre g")
    List<String> findAllGenreNames();
}
