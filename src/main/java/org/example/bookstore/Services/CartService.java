package org.example.bookstore.Services;

import org.example.bookstore.Entities.*;
import org.example.bookstore.Entities.User;
import org.example.bookstore.repository.BookRepository;
import org.example.bookstore.repository.CartBookRepository;
import org.example.bookstore.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartBookRepository cartBookRepository;

    private final BookRepository bookRepository;

    @Autowired
    public CartService(BookRepository bookRepository, CartRepository cartRepository, CartBookRepository cartBookRepository) {
        this.bookRepository = bookRepository;
        this.cartBookRepository = cartBookRepository;
        this.cartRepository=cartRepository;
    }

    // Створення нового кошика
    public Cart createCart(User user) {

        // Перевірка, чи є вже кошик у користувача
        Optional<Cart> existingCart = cartRepository.findByUserId(user.getId());
        if (existingCart.isPresent()) {
            return existingCart.get();  // Повертаємо вже існуючий кошик
        }

        // Створюємо новий кошик
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(0);  // Початкова вартість кошика
        return cartRepository.save(cart);
    }

    // Додати книгу до кошика
    public Cart addBookToCart(Long cartId, Long bookId, int quantity) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Перевірка наявності книги на складі
        if (book.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available for the book.");
        }

        // Перевірка чи книга вже є в кошику
        Optional<CartBook> existingCartBook = cartBookRepository.findByCartIdAndBookId(cartId, bookId);
        if (existingCartBook.isPresent()) {

            throw new RuntimeException("This book is already in the cart.");
        } else {

            //додавання книги до кошика
            CartBook cartBook = new CartBook();
            cartBook.setCart(cart);
            cartBook.setBook(book);
            cartBook.setQuantity(quantity);
            cartBook.setPricePerBook(book.getPrice());
            cartBookRepository.save(cartBook);
        }

        // Оновлення загальної вартості кошика
        updateCartTotalPrice(cartId);

        return cart;
    }

    public Cart updateBookQuantityInCart(Long cartId, Long bookId, int newQuantity) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Перевірка наявності книги на складі
        if (book.getStockQuantity() < newQuantity) {
            throw new RuntimeException("Not enough stock available to set this quantity.");
        }


        CartBook cartBook = cartBookRepository.findByCartIdAndBookId(cartId, bookId)
                .orElseThrow(() -> new RuntimeException("Book not in cart"));

        // Оновлення кількості книг
        cartBook.setQuantity(newQuantity);
        cartBookRepository.save(cartBook);

        // Оновлення загальної вартості кошика
        updateCartTotalPrice(cartId);

        return cart;
    }

    // Видалити книгу з кошика
    public Cart removeBookFromCart(Long cartId, Long bookId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartBook cartBook = cartBookRepository.findByCartIdAndBookId(cartId, bookId)
                .orElseThrow(() -> new RuntimeException("Book not in cart"));

        // Видалення книги з кошика
        cartBookRepository.delete(cartBook);

        // Оновлення загальної вартості кошика
        updateCartTotalPrice(cartId);

        return cart;
    }

    // Оновити загальну вартість кошика
    private void updateCartTotalPrice(Long cartId) {

        Integer totalPrice = cartBookRepository.calculateTotalPrice(cartId);

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }

    // Отримати кошик користувача
    public Cart getCartByUserId(Long userId) {

        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

   }
