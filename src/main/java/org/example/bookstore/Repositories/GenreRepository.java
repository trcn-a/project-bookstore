package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторій для роботи з сутністю Genre.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій.
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {
    // Тут можна додавати додаткові методи для специфічних запитів, якщо потрібно.
}
