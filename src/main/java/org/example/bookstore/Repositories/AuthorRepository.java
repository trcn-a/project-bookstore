package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторій для роботи з сутністю Author.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій з авторами.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByName(String name);
}
