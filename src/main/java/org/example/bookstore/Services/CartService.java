package org.example.bookstore.Services;

import org.example.bookstore.Entities.*;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Repositories.CartBookRepository;
import org.example.bookstore.Repositories.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartBookRepository cartBookRepository;

    public CartService(CartRepository cartRepository, CartBookRepository cartBookRepository) {
        this.cartRepository = cartRepository;
        this.cartBookRepository = cartBookRepository;
    }


    private Cart getOrCreateCart(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Користувач не авторизований.");
        }
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public void addOrUpdateBookInCart(User user, Book book, int quantity) {
        if (user == null) {
            throw new IllegalArgumentException("Користувач не авторизований.");
        }
        if (book.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("Книга наразі відсутня на складі.");
        }

        // Обмеження кількості книг в кошику — не більше 10 одиниць
        if (quantity > 10) {
            throw new IllegalArgumentException("Максимальна кількість книг в одному замовленні - 10.");
        }

        // Перевірка на те, чи кількість, яку додає користувач, не перевищує наявну кількість на складі
        if (quantity > book.getStockQuantity()) {
            throw new IllegalArgumentException("Недостатньо книг на складі.");
        }


        Cart cart = getOrCreateCart(user);

        CartBook cartBook = cartBookRepository.findByCartIdAndBookId(cart.getId(), book.getId())
                .orElseGet(() -> {
                    CartBook newCartBook = new CartBook();
                    newCartBook.setCart(cart);
                    newCartBook.setBook(book);
                    return newCartBook;
                });

       cartBook.setQuantity(quantity);
        cartBookRepository.save(cartBook);
    }


    @Transactional
    public void removeBookFromCart(User user, Book book) {
        if (user == null) {
            throw new IllegalArgumentException("Користувач не авторизований.");
        }
        Cart cart = getOrCreateCart(user);

        // Перевірка на наявність книги в кошику перед видаленням
        Optional<CartBook> cartBook = cartBookRepository.findByCartIdAndBookId(cart.getId(), book.getId());
        if (cartBook.isPresent()) {
            cartBookRepository.deleteByCartIdAndBookId(cart.getId(), book.getId());
        } else {
            throw new IllegalArgumentException("Ця книга відсутня в кошику.");
        }
    }



    public List<CartBook> getCartContents(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Користувач не авторизований.");
        }
        Cart cart = getOrCreateCart(user);
        return cartBookRepository.findByCartId(cart.getId());
    }

    public double getTotalSumForCart(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Користувач не авторизований.");
        }
        Cart cart = getOrCreateCart(user);
        Integer totalAmount = cartBookRepository.calculateTotalSumByCartId(cart.getId());
        return totalAmount != null ? totalAmount : 0;
    }
}
