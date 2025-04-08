package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpSession;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Services.CartService;
import org.example.bookstore.Services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class CartFavoriteAdvice {

    private final CartService cartService;
    private final FavoriteService favoriteService;

    @Autowired
    public CartFavoriteAdvice(CartService cartService, FavoriteService favoriteService) {
        this.cartService = cartService;
        this.favoriteService = favoriteService;
    }

    @ModelAttribute("cartBookIds")
    public Set<Long> getCartBookIds(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) return Set.of();

        return cartService.getCartContents(user)
                .stream()
                .map(cartBook -> cartBook.getBook().getId())
                .collect(Collectors.toSet());
    }

    @ModelAttribute("favoriteBookIds")
    public Set<Long> getFavoriteBookIds(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) return Set.of();

        return favoriteService.getFavoriteBooks(user.getId())
                .stream()
                .map(Book::getId)
                .collect(Collectors.toSet());
    }
}
