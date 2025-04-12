package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторій для роботи з сутністю Author.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій з авторами.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {

}
