package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.User;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String getFavorites(Model model, @SessionAttribute("user") User user) {
        model.addAttribute("favorites", favoriteService.getFavoriteBooks(user.getId()));
        return "favorites";
    }

    @PostMapping("/add/{bookId}")
    public String addToFavorites(@PathVariable Long bookId,
                                 @SessionAttribute("user") User user,
                                 @RequestParam(required = false) String fromPage,
                                 @RequestHeader(value = "X-Requested-With", required = false) String requestedWith, Model model) {
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

    @PostMapping("/remove/{bookId}")
    public String removeFromFavorites(@PathVariable Long bookId,
                                      @SessionAttribute("user") User user,
                                      @RequestParam(required = false) String fromPage,
                                      @RequestHeader(value = "X-Requested-With", required = false) String requestedWith, Model model) {
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

