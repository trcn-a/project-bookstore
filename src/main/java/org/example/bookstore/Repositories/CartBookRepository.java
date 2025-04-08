package org.example.bookstore.Repositories;


import org.example.bookstore.Entities.CartBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CartBookRepository extends JpaRepository<CartBook, Long> {

    Optional<CartBook> findByCartIdAndBookId(Long cartId, Long bookId);
    void deleteByCartIdAndBookId(Long cartId, Long bookId);

    List<CartBook> findByCartId(Long id);


    @Query("SELECT SUM(cb.book.actualPrice * cb.quantity) FROM CartBook cb WHERE cb.cart.id = :cartId")
    Integer calculateTotalSumByCartId(@Param("cartId") Long cartId);


}


