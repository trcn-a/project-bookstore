package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpSession;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Services.CartService;
import org.example.bookstore.Services.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Глобальний контролер для надання даних про книги в кошику та обраному для всіх контролерів.
 * Використовує @ControllerAdvice для забезпечення доступу до атрибутів у всіх запитах.
 */
@ControllerAdvice
public class CartFavoriteAdvice {

    private static final Logger logger = LoggerFactory.getLogger(CartFavoriteAdvice.class);

    private final CartService cartService;
    private final FavoriteService favoriteService;

    /**
     * Конструктор контролера, який приймає сервіси для роботи з кошиком та обраними книгами через інʼєкцію залежності.
     *
     * @param cartService сервіс для роботи з кошиком
     * @param favoriteService сервіс для роботи з обраними книгами
     */
    @Autowired
    public CartFavoriteAdvice(CartService cartService, FavoriteService favoriteService) {
        this.cartService = cartService;
        this.favoriteService = favoriteService;
    }

    /**
     * Надання атрибута {@code cartBookIds} для всіх контролерів.
     * Повертає множину ідентифікаторів книг, що знаходяться в кошику користувача.
     *
     * @param session сесія користувача
     * @return множина ідентифікаторів книг, що знаходяться в кошику
     */
    @ModelAttribute("cartBookIds")
    public Set<Long> getCartBookIds(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            logger.debug("User not logged in, returning empty cart.");
            return Set.of();
        }

        Set<Long> cartBookIds = cartService.getCartContents(user)
                .stream()
                .map(cartBook -> cartBook.getBook().getId())
                .collect(Collectors.toSet());

        logger.debug("Cart for user {}: {}", user.getId(), cartBookIds);

        return cartBookIds;
    }

    /**
     * Надання атрибута {@code favoriteBookIds} для всіх контролерів.
     * Повертає множину ідентифікаторів обраних книг користувача.
     *
     * @param session сесія користувача
     * @return множина ідентифікаторів обраних книг
     */
    @ModelAttribute("favoriteBookIds")
    public Set<Long> getFavoriteBookIds(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            logger.debug("User not logged in, returning empty favorites.");
            return Set.of();
        }

        Set<Long> favoriteBookIds = favoriteService.getFavoriteBooks(user.getId())
                .stream()
                .map(Book::getId)
                .collect(Collectors.toSet());

        logger.debug("Favorites for user {}: {}", user.getId(), favoriteBookIds);

        return favoriteBookIds;
    }
}
