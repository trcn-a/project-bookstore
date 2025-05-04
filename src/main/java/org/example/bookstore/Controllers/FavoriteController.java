package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.User;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Services.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.stream.Collectors;

/**
 * Контролер для обробки додавання та видалення книг з обраних користувача.
 */
@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    private final FavoriteService favoriteService;

    /**
     * Конструктор контролера, який приймає сервіс для роботи з обраними книгами через інʼєкцію залежності.
     *
     * @param favoriteService сервіс для роботи з обраними книгами
     */
    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * Відображає список обраних книг користувача.
     *
     * @param model модель для передачі атрибутів в представлення
     * @param user  авторизований користувач, отриманий з сесії
     * @return сторінка з обраними книгами користувача
     */
    @GetMapping
    public String getFavorites(Model model, @SessionAttribute(value = "user", required = false) User user) {
         if (user == null) {
            logger.warn("User is not authenticated. Redirecting to login.");
            return "redirect:/login";
        }
        try {
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
                                 @SessionAttribute("user") User user,
                                 Model model) {
        logger.info("User {} is adding book {} to favorites", user.getId(), bookId);

        try {
            favoriteService.addToFavorites(user.getId(), bookId);

                model.addAttribute("book", bookId);
                model.addAttribute("favoriteBookIds", favoriteService.getFavoriteBookIds(user.getId()) );



            return "fragments/favorite-button :: favorite-button";

        } catch (Exception e) {
            throw new RuntimeException("Error adding book to favorites. Book ID: "
                    + bookId + ", User ID: " + user.getId(), e);

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
                                      @SessionAttribute("user") User user,
                                      @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                      Model model) {
        logger.info("User {} is removing book {} from favorites", user.getId(), bookId);

        try {
            favoriteService.removeFromFavorites(user.getId(), bookId);

            if ("XMLHttpRequest".equals(requestedWith)) {
                logger.debug("AJAX request detected while removing book {}", bookId);
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
