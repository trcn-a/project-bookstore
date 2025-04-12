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

/**
 * Контролер для обробки запитів, пов'язаних з кошиком користувача.
 * Цей контролер відповідає за перегляд, додавання та видалення товарів у кошику.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

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
        // Якщо користувач не авторизований, перенаправляємо на сторінку логіну
        if (user == null) {
            return "redirect:/user/login";
        }

        // Отримуємо вміст кошика та загальну суму
        List<CartBook> cartBooks = cartService.getCartContents(user);
        double totalSum = cartService.getTotalSumForCart(user);

        // Додаємо дані до моделі
        model.addAttribute("cartBooks", cartBooks);
        model.addAttribute("totalSum", totalSum);
        return "cart";
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
        // Якщо користувач не авторизований, перенаправляємо на сторінку логіну
        if (user == null) {
            return "redirect:/user/login";
        }

        // Отримуємо книгу за ID
        Book book = bookService.getBookById(bookId);

        // Додаємо або оновлюємо книгу в кошику
        cartService.addOrUpdateBookInCart(user, book, quantity);

        // Якщо це AJAX-запит, повертаємо частину HTML для оновлення кошика
        if ("XMLHttpRequest".equals(requestedWith)) {
            model.addAttribute("book", book);
            model.addAttribute("fromPage", fromPage);
            model.addAttribute("cartBookIds", cartService.getCartContents(user)
                    .stream()
                    .map(cartBook -> cartBook.getBook().getId())
                    .collect(Collectors.toSet()));
            return "fragments/cart-button :: cart-button";
        }

        // В іншому випадку, перенаправляємо на сторінку кошика або на попередню сторінку
        return "redirect:" + (fromPage != null ? fromPage : "/cart");
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
        // Отримуємо книгу за ID
        Book book = bookService.getBookById(bookId);

        // Видаляємо книгу з кошика
        cartService.removeBookFromCart(user, book);

        // Якщо це AJAX-запит, повертаємо частину HTML для оновлення кошика
        if ("XMLHttpRequest".equals(requestedWith)) {
            model.addAttribute("book", book);
            model.addAttribute("fromPage", fromPage);
            model.addAttribute("cartBookIds", cartService.getCartContents(user)
                    .stream()
                    .map(cartBook -> cartBook.getBook().getId())
                    .collect(Collectors.toSet()));
            return "fragments/cart-button :: cart-button";
        }

        // В іншому випадку, перенаправляємо на сторінку кошика або на попередню сторінку
        return "redirect:" + (fromPage != null ? fromPage : "/cart");
    }
}
