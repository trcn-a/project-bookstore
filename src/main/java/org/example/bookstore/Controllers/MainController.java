package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpSession;
import org.example.bookstore.DTOs.BookDTO;
import org.example.bookstore.Entities.Author;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.Review;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.AuthorService;
import org.example.bookstore.Services.BookService;
import org.example.bookstore.Services.PriceCalculationService;
import org.example.bookstore.Services.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final BookService bookService;
    private final AuthorService authorService;
    private final ReviewService reviewService;
    private final PriceCalculationService priceCalculationService;

    /**
     * Конструктор контролера.
     *
     * @param bookService сервіс для роботи з книгами
     * @param authorService сервіс для роботи з авторами
     * @param reviewService сервіс для роботи з відгуками
     * @param priceCalculationService сервіс для роботи з розрахунками цін
     */
    @Autowired
    public MainController(BookService bookService, AuthorService authorService, ReviewService reviewService, PriceCalculationService priceCalculationService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.reviewService = reviewService;
        this.priceCalculationService = priceCalculationService;
    }

    /**
     * Обробляє запит до головної сторінки з можливістю сортування та пагінації.
     *
     * @param session HTTP-сесія користувача
     * @param model модель для передачі даних у шаблон
     * @param page номер сторінки (за замовчуванням 0)
     * @param size кількість елементів на сторінку (за замовчуванням 2)
     * @param sort параметр сортування у форматі "поле-напрямок" (наприклад, "title-asc")
     * @return назва HTML-шаблону "index"
     */
    @GetMapping("/")
    public String homePage(HttpSession session, Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "2") int size,
                           @RequestParam(defaultValue = "title-asc") String sort) {
        try {
            String[] sortParams = sort.split("-");
            String sortBy = sortParams[0];
            boolean ascending = "asc".equalsIgnoreCase(sortParams[1]);

            Page<Book> bookPage = bookService.getSortedBooks(sortBy, ascending, page, size);

            Page<BookDTO> bookDTOPage = bookPage.map(priceCalculationService::convertToBookDTO);

            model.addAttribute("books", bookDTOPage);
            model.addAttribute("sort", sort);

            logger.info("User {} visited home page. Sort: {}, Page: {}, Size: {}",
                    session.getAttribute("user") != null ? ((User) session.getAttribute("user")).getId() : "guest",
                    sort, page, size);

            return "index";
        } catch (Exception ex) {
            throw new RuntimeException("Error loading home page: sort="
                    + sort + ", page=" + page + ", size=" + size, ex);
        }
    }


    /**
     * Показує деталі книги, її середню оцінку та список відгуків.
     * Якщо користувач авторизований — також його персональний відгук.
     *
     * @param id ідентифікатор книги
     * @param model модель для передачі даних у шаблон
     * @param session HTTP-сесія користувача
     * @return назва HTML-шаблону "book"
     */
    @GetMapping("/book/{id}")
    public String bookDetails(@PathVariable Long id, Model model, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");

            Book book = bookService.getBookById(id);
            model.addAttribute("book", book);
            model.addAttribute("averageRating", reviewService.getAverageRating(id));
            model.addAttribute("reviews", reviewService.getReviewsByBook(id));
            model.addAttribute("actualPrice", priceCalculationService.calculateActualPrice(book));

            if (user != null) {
                Review userReview = reviewService.getReviewByBookIdAndUserId(id, user.getId());
                if (userReview != null) {
                    model.addAttribute("userReview", userReview);
                }
            }

            logger.info("User {} viewed book {}.",
                    user != null ? user.getId() : "guest", book.getTitle());

            return "book";
        } catch (Exception ex) {
            throw new RuntimeException("Error loading book details. Book ID: " + id, ex);
        }
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
        try {
            Author author = authorService.getAuthorById(id);
            model.addAttribute("author", author);

            List<Book> books = bookService.getBooksByAuthor(id);
            List<BookDTO> bookDTOs = priceCalculationService.convertToBookDTOs(books);
            model.addAttribute("books", bookDTOs);

            logger.info("User {} viewed author {}.",
                    session.getAttribute("user") != null ? ((User) session.getAttribute("user")).getId() : "guest",
                    author.getName());

            return "author";
        } catch (Exception ex) {
            throw new RuntimeException("Error loading author details. Author ID: " + id, ex);
        }
    }

}
