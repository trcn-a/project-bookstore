package org.example.bookstore.controller;

import org.example.bookstore.config.CustomUserDetails;
import org.example.bookstore.entity.*;
import org.example.bookstore.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final PublisherService publisherService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;
    private final CartService cartService;

    @Autowired
    public MainController(BookService bookService, AuthorService authorService,
                          GenreService genreService, PublisherService publisherService,
                          ReviewService reviewService, FavoriteService favoriteService,
                          CartService cartService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
        this.publisherService = publisherService;
        this.reviewService = reviewService;
        this.favoriteService = favoriteService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String homePage(Model model,
                           @RequestParam(required = false) List<String> authors,
                           @RequestParam(required = false) List<String> genres,
                           @RequestParam(required = false) List<String> publishers,
                           @RequestParam(required = false) Integer minPrice,
                           @RequestParam(required = false) Integer maxPrice,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "5") int size,
                           @RequestParam(defaultValue = "title-asc") String sort,
                           @AuthenticationPrincipal CustomUserDetails currentUser,
                           @SessionAttribute(value = "guestCart", required = false) List<CartBook> guestCart) {
        try {
            String[] sortParams = sort.split("-");
            String sortBy = sortParams[0];
            boolean ascending = "asc".equalsIgnoreCase(sortParams[1]);

            Page<Book> books = bookService.filterAndSortBooks(authors, genres, publishers,
                    minPrice, maxPrice, sortBy, ascending, page, size);

            model.addAttribute("books", books);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", books.getTotalPages());
            model.addAttribute("sort", sort);

            User user = currentUser != null ? currentUser.getUser() : null;
            setUserCartAndFavorites(model, user, guestCart);

            model.addAttribute("allAuthors", authorService.getAllAuthorNames());
            model.addAttribute("allGenres", genreService.getAllGenreNames());
            model.addAttribute("allPublishers", publisherService.getAllPublisherNames());

            model.addAttribute("authors", authors);
            model.addAttribute("genres", genres);
            model.addAttribute("publishers", publishers);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);

            return "index";
        } catch (Exception ex) {
            throw new RuntimeException("Error loading home page: sort=" + sort +
                    ", page=" + page + ", size=" + size, ex);
        }
    }

    @GetMapping("/book/{id}")
    public String bookDetails(@PathVariable Long id, Model model,
                              @AuthenticationPrincipal CustomUserDetails currentUser,
                              @SessionAttribute(value = "guestCart", required = false) List<CartBook> guestCart) {
        try {
            User user = currentUser != null ? currentUser.getUser() : null;

            Book book = bookService.getBookById(id);
            model.addAttribute("book", book);
            model.addAttribute("averageRating", reviewService.getAverageRating(id));
            model.addAttribute("reviews", reviewService.getReviewsByBook(id));

            if (user != null) {
                Review userReview = reviewService.getReviewByBookIdAndUserId(id, user.getId());
                if (userReview != null) {
                    model.addAttribute("userReview", userReview);
                }
            }

            setUserCartAndFavorites(model, user, guestCart);

            logger.info("User {} viewed book {}.",
                    user != null ? user.getId() : "guest", book.getTitle());

            return "book";
        } catch (Exception ex) {
            throw new RuntimeException("Error loading book details. Book ID: " + id, ex);
        }
    }

    @GetMapping("/author/{id}")
    public String authorDetails(@PathVariable Long id, Model model,
                                @AuthenticationPrincipal CustomUserDetails currentUser,
                                @SessionAttribute(value = "guestCart", required = false) List<CartBook> guestCart) {
        try {
            User user = currentUser != null ? currentUser.getUser() : null;

            Author author = authorService.getAuthorById(id);
            model.addAttribute("author", author);

            List<Book> books = bookService.getBooksByAuthor(id);
            model.addAttribute("books", books);

            double rating = authorService.getAuthorAverageRating(id);
            model.addAttribute("rating", rating);
            int countReview = authorService.getAuthorCountRating(id);
            model.addAttribute("countReview", countReview);
            setUserCartAndFavorites(model, user, guestCart);

            logger.info("User {} viewed author {}.",
                    user != null ? user.getId() : "guest", author.getName());

            return "author";
        } catch (Exception ex) {
            throw new RuntimeException("Error loading author details. Author ID: " + id, ex);
        }
    }

    private void setUserCartAndFavorites(Model model, User user, List<CartBook> guestCart) {
        if (user != null) {
            model.addAttribute("favoriteBookIds", favoriteService.getFavoriteBookIds(user.getId()));
            model.addAttribute("cartBookIds", cartService.getCartBookIds(user));
        } else {
            model.addAttribute("cartBookIds",
                    guestCart != null ? guestCart.stream().map(c -> c.getBook().getId()).toList() : List.of());
        }
    }
}
