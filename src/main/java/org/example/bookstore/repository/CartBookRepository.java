package org.example.bookstore.repository;


import org.example.bookstore.Entities.CartBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CartBookRepository extends JpaRepository<CartBook, Long> {

    Optional<CartBook> findByCartIdAndBookId(Long cartId, Long bookId);


    @Query("SELECT SUM(cb.quantity * cb.pricePerBook) FROM CartBook cb WHERE cb.cart.id = :cartId")
    Integer calculateTotalPrice(@Param("cartId") Long cartId);

    List<CartBook> findByCartId(Long id);

}


