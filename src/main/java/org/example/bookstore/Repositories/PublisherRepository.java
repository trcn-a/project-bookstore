package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}
