package org.example.bookstore.controller;

import org.example.bookstore.config.CustomUserDetails;
import org.example.bookstore.entity.User;
import org.example.bookstore.service.CartService;
import org.example.bookstore.service.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Контролер для обробки додавання та видалення книг з обраних користувача.
 */
@Controller
@RequestMapping("/favorites")
public class FavoriteController {


    private final FavoriteService favoriteService;
    private final CartService cartService;


    /**
     * Конструктор контролера, який приймає сервіс для роботи з обраними книгами через інʼєкцію залежності.
     *
     * @param favoriteService сервіс для роботи з обраними книгами
     */
    @Autowired
    public FavoriteController(FavoriteService favoriteService, CartService cartService) {
        this.favoriteService = favoriteService;
        this.cartService = cartService;
    }


    @GetMapping
    public String getFavorites(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        try {
            model.addAttribute("cartBookIds", cartService.getCartBookIds(user));

            model.addAttribute("favorites", favoriteService.getFavoriteBooks(user.getId()));
            return "favorites";
        } catch (Exception e) {
            throw new RuntimeException("Error fetching favorites for user ID: " + user.getId(), e);
        }
    }

    /**
     * Додає книгу до обраних.
     *
     * @param bookId      ідентифікатор книги
     * @param user        авторизований користувач, отриманий з сесії
     * @param fromPage    сторінка, з якої було здійснено запит (опціонально)
     * @param requestedWith тип запиту (для AJAX-запитів)
     * @param model       модель для передачі атрибутів в представлення
     * @return перенаправлення на відповідну сторінку або оновлення фрагменту для AJAX-запитів
     */
    @PostMapping("/add/{bookId}")
    public String addToFavorites(@PathVariable Long bookId,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model) {
        User user = userDetails.getUser();

        try {
            favoriteService.addToFavorites(user.getId(), bookId);
            model.addAttribute("book", bookId);
            model.addAttribute("favoriteBookIds", favoriteService.getFavoriteBookIds(user.getId()));
            return "fragments/favorite-button :: favorite-button";
        } catch (Exception e) {
            throw new RuntimeException("Error adding book to favorites. Book ID: " + bookId + ", User ID: " + user.getId(), e);
        }
    }

    /**
     * Видаляє книгу з обраних.
     *
     * @param bookId      ідентифікатор книги
     * @param user        авторизований користувач, отриманий з сесії
     * @param fromPage    сторінка, з якої було здійснено запит (опціонально)
     * @param requestedWith тип запиту (для AJAX-запитів)
     * @param model       модель для передачі атрибутів в представлення
     * @return перенаправлення на відповідну сторінку або оновлення фрагменту для AJAX-запитів
     */
    @PostMapping("/remove/{bookId}")
    public String removeFromFavorites(@PathVariable Long bookId,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                      Model model) {
        User user = userDetails.getUser();
        System.out.println("Requested-With: " + requestedWith);

        try {
            favoriteService.removeFromFavorites(user.getId(), bookId);

            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("book", bookId);
                model.addAttribute("favoriteBookIds", favoriteService.getFavoriteBookIds(user.getId()) );

                return "fragments/favorite-button :: favorite-button";
            }

            return "redirect:/favorites";

        } catch (Exception e) {
            throw new RuntimeException("Error removing book from favorites. Book ID: "
                    + bookId + ", User ID: " + user.getId(), e);

        }
    }
}
