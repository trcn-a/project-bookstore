package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpSession;
import org.example.bookstore.Entities.Author;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.Review;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.AuthorService;
import org.example.bookstore.Services.BookService;
import org.example.bookstore.Services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * Контролер для обробки запитів до головної сторінки,
 * сторінки книги та сторінки автора.
 */
@Controller
public class MainController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final ReviewService reviewService;

    /**
     * Конструктор контролера, що інʼєктує сервіси для роботи з книгами, авторами та відгуками.
     *
     * @param bookService сервіс для роботи з книгами
     * @param authorService сервіс для роботи з авторами
     * @param reviewService сервіс для роботи з відгуками
     */
    @Autowired
    public MainController(BookService bookService, AuthorService authorService, ReviewService reviewService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.reviewService = reviewService;
    }

    /**
     * Обробляє запит до головної сторінки магазину з можливістю сортування та пагінації.
     *
     * @param session HTTP-сесія користувача
     * @param model модель для передачі даних у шаблон
     * @param page номер сторінки для пагінації
     * @param size кількість елементів на сторінку
     * @param sort параметр сортування у форматі "поле-напрямок" (наприклад, "title-asc")
     * @return назва HTML-шаблону "index"
     */
    @GetMapping("/")
    public String homePage(HttpSession session, Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "2") int size,
                           @RequestParam(defaultValue = "title-asc") String sort
    ) {
        String[] sortParams = sort.split("-");
        String sortBy = sortParams[0];
        boolean ascending = "asc".equals(sortParams[1]);

        model.addAttribute("books", bookService.getSortedBooks(sortBy, ascending, page, size));
        model.addAttribute("sort", sort);
        return "index";
    }

    /**
     * Показує деталі книги, її середню оцінку та список відгуків.
     * Якщо користувач авторизований — показує його персональний відгук (якщо є).
     *
     * @param id ідентифікатор книги
     * @param model модель для передачі даних у шаблон
     * @param session HTTP-сесія користувача
     * @return назва HTML-шаблону "book"
     */
    @GetMapping("/book/{id}")
    public String bookDetails(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

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

        return "book";
    }

    /**
     * Показує інформацію про автора та список його книг.
     *
     * @param id ідентифікатор автора
     * @param model модель для передачі даних у шаблон
     * @param session HTTP-сесія користувача
     * @return назва HTML-шаблону "author"
     */
    @GetMapping("/author/{id}")
    public String authorDetails(@PathVariable Long id, Model model, HttpSession session) {
        Author author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
        List<Book> books = bookService.getBooksByAuthor(id);
        model.addAttribute("books", books);
        return "author";
    }

}
