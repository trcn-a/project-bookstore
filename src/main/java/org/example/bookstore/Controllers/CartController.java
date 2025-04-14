package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Services.BookService;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Контролер для обробки запитів, пов'язаних з кошиком користувача.
 * Цей контролер відповідає за перегляд, додавання та видалення товарів у кошику.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final BookService bookService;

    /**
     * Конструктор класу.
     * @param cartService Сервіс для роботи з кошиком.
     * @param bookService Сервіс для роботи з книгами.
     */
    @Autowired
    public CartController(CartService cartService, BookService bookService) {
        this.cartService = cartService;
        this.bookService = bookService;
    }

    /**
     * Відображає вміст кошика користувача.
     * Якщо користувач не авторизований, перенаправляє на сторінку логіну.
     * @param user Користувач, що знаходиться в сесії.
     * @param model Модель для передачі даних на шаблон.
     * @return Назва шаблону для рендерингу сторінки кошика.
     */
    @GetMapping
    public String viewCart(@SessionAttribute(value = "user", required = false) User user, Model model) {
        if (user == null) {
            logger.warn("User not logged in, redirecting to login page.");
            return "redirect:/user/login";
        }

        try {
            List<CartBook> cartBooks = cartService.getCartContents(user);
            double totalSum = cartService.getTotalSumForCart(user);

            model.addAttribute("cartBooks", cartBooks);
            model.addAttribute("totalSum", totalSum);
            logger.info("User {} viewed the cart.", user.getId());

            return "cart";
        } catch (Exception e) {
            logger.error("Error viewing cart for user {}: {}", user.getId(), e.getMessage(), e);
            return "error";
        }
    }

    /**
     * Додає або оновлює книгу в кошику.
     * Якщо запит зроблено через AJAX, повертається частина HTML для оновлення кошика на сторінці.
     * @param user Користувач, що знаходиться в сесії.
     * @param bookId Ідентифікатор книги, яку додають до кошика.
     * @param quantity Кількість книг, яку додають до кошика.
     * @param requestedWith Заголовок запиту для визначення чи це AJAX-запит.
     * @param fromPage Сторінка, з якої був зроблений запит (для редиректу після операції).
     * @param model Модель для передачі даних на шаблон.
     * @return Перенаправлення на відповідну сторінку або частину HTML для AJAX-запиту.
     */
    @PostMapping("/add")
    public String addBookToCart(@SessionAttribute(value = "user", required = false) User user,
                                @RequestParam("bookId") Long bookId,
                                @RequestParam("quantity") int quantity,
                                @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                @RequestParam(required = false) String fromPage,
                                Model model) {
        if (user == null) {
            logger.warn("User not logged in, redirecting to login page.");
            return "redirect:/user/login";
        }

        try {
            Book book = bookService.getBookById(bookId);
            cartService.addOrUpdateBookInCart(user, book, quantity);
            logger.info("User {} added {} of book '{}' to cart.", user.getId(), quantity, book.getTitle());

            if ("XMLHttpRequest".equals(requestedWith)) {
                logger.debug("AJAX request detected while adding book {}", bookId);

                model.addAttribute("book", book);
                model.addAttribute("fromPage", fromPage);
                model.addAttribute("cartBookIds", cartService.getCartContents(user)
                        .stream()
                        .map(cartBook -> cartBook.getBook().getId())
                        .collect(Collectors.toSet()));
                return "fragments/cart-button :: cart-button";
            }

            return "redirect:" + (fromPage != null ? fromPage : "/cart");
        } catch (Exception e) {
            logger.error("Error adding book {} to cart for user {}: {}", bookId, user.getId(), e.getMessage(), e);
            return "error";
        }
    }

    /**
     * Видаляє книгу з кошика.
     * Якщо запит зроблено через AJAX, повертається частина HTML для оновлення кошика на сторінці.
     * @param user Користувач, що знаходиться в сесії.
     * @param bookId Ідентифікатор книги, яку видаляють з кошика.
     * @param requestedWith Заголовок запиту для визначення чи це AJAX-запит.
     * @param fromPage Сторінка, з якої був зроблений запит (для редиректу після операції).
     * @param model Модель для передачі даних на шаблон.
     * @return Перенаправлення на відповідну сторінку або частину HTML для AJAX-запиту.
     */
    @PostMapping("/remove")
    public String removeBookFromCart(@SessionAttribute("user") User user,
                                     @RequestParam("bookId") Long bookId,
                                     @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                     @RequestParam(required = false) String fromPage,
                                     Model model) {
        try {
            Book book = bookService.getBookById(bookId);
            cartService.removeBookFromCart(user, book);
            logger.info("User {} removed book '{}' from cart.", user.getId(), book.getTitle());

            if ("XMLHttpRequest".equals(requestedWith)) {
                logger.debug("AJAX request detected while removing book {}", bookId);

                model.addAttribute("book", book);
                model.addAttribute("fromPage", fromPage);
                model.addAttribute("cartBookIds", cartService.getCartContents(user)
                        .stream()
                        .map(cartBook -> cartBook.getBook().getId())
                        .collect(Collectors.toSet()));
                return "fragments/cart-button :: cart-button";
            }

            return "redirect:" + (fromPage != null ? fromPage : "/cart");
        } catch (Exception e) {
            logger.error("Error removing book {} from cart for user {}: {}", bookId, user.getId(), e.getMessage(), e);
            return "error";
        }
    }
}
