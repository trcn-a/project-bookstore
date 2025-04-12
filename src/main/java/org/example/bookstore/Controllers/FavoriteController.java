package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.User;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.stream.Collectors;

/**
 * Контролер для обробки додавання та видалення книг з обраних користувача.
 */
@Controller
@RequestMapping("/favorites")
public class FavoriteController {

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
    public String getFavorites(Model model, @SessionAttribute("user") User user) {
        model.addAttribute("favorites", favoriteService.getFavoriteBooks(user.getId()));
        return "favorites";
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
                                 @RequestParam(required = false) String fromPage,
                                 @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                 Model model) {
        favoriteService.addToFavorites(user.getId(), bookId);

        if ("XMLHttpRequest".equals(requestedWith)) {
            model.addAttribute("book", bookId);
            model.addAttribute("fromPage", fromPage);
            model.addAttribute("favoriteBookIds", favoriteService.getFavoriteBooks(user.getId())
                    .stream()
                    .map(Book::getId)
                    .collect(Collectors.toSet()));
            return "fragments/favorite-button :: favorite-button";
        }

        return "redirect:" + (fromPage != null ? fromPage : "/favorites");
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
                                      @RequestParam(required = false) String fromPage,
                                      @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                      Model model) {
        favoriteService.removeFromFavorites(user.getId(), bookId);

        if ("XMLHttpRequest".equals(requestedWith)) {
            model.addAttribute("book", bookId);
            model.addAttribute("fromPage", fromPage);
            model.addAttribute("favoriteBookIds", favoriteService.getFavoriteBooks(user.getId())
                    .stream()
                    .map(Book::getId)
                    .collect(Collectors.toSet()));
            return "fragments/favorite-button :: favorite-button";
        }

        return "redirect:" + (fromPage != null ? fromPage : "/favorites");
    }
}
