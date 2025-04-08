package org.example.bookstore.Repositories;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.Favorite;
import org.example.bookstore.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    Optional<Favorite> findByUserAndBook(User user, Book book);
    void deleteByUserAndBook(User user, Book book);
}
