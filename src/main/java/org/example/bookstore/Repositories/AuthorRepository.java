package org.example.bookstore.Repositories;

import org.example.bookstore.Entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuthorRepository extends JpaRepository<Author, Long> {
}
