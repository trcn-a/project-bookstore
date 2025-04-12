package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторій для роботи з сутністю Publisher.
 * Використовує Spring Data JPA для автоматичного виконання CRUD операцій.
 */
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}
