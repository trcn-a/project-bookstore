package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpSession;
import org.example.bookstore.Config.CustomUserDetails;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.BookService;
import org.example.bookstore.Services.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Контролер для обробки запитів, пов'язаних з кошиком користувача.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final BookService bookService;

    @Autowired
    public CartController(CartService cartService, BookService bookService) {
        this.cartService = cartService;
        this.bookService = bookService;
    }

    /**
     * Відображає вміст кошика користувача (авторизованого або гостьового).
     */
    @GetMapping
    public String viewCart(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                           HttpSession session,
                           Model model) {
        try {
            List<CartBook> cartBooks;
            double totalSum;

            if (customUserDetails != null) {
                User user = customUserDetails.getUser(); // Отримуємо користувача з CustomUserDetails
                cartBooks = cartService.getCartContents(user);
                totalSum = cartService.getTotalSumForCart(user);
            } else {
                cartBooks = getGuestCart(session);
                totalSum = cartService.getTotalSumForGuestCart(cartBooks);
            }

            model.addAttribute("cartBooks", cartBooks);
            model.addAttribute("totalSum", totalSum);
            return "cart";
        } catch (Exception ex) {
            logger.error("Error loading cart", ex);
            throw new RuntimeException("Error loading cart", ex);
        }
    }

    /**
     * Додає або оновлює книгу в кошику.
     */
    @PostMapping("/add")
    public String addBookToCart(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                @RequestParam("bookId") Long bookId,
                                @RequestParam("quantity") int quantity,
                                @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                HttpSession session,
                                Model model) {
        try {
            Book book = bookService.getBookById(bookId);

            if (customUserDetails != null) {
                User user = customUserDetails.getUser(); // Отримуємо користувача з CustomUserDetails
                cartService.addOrUpdateBookInCart(user, book, quantity);
                model.addAttribute("cartBookIds", cartService.getCartBookIds(user));
            } else {
                List<CartBook> guestCart = getGuestCart(session);
                guestCart = cartService.addOrUpdateBookInGuestCart(guestCart, book, quantity);
                session.setAttribute("guestCart", guestCart);
                model.addAttribute("cartBookIds", guestCart.stream().map(c -> c.getBook().getId()).toList());
            }

            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("book", book);
                return "fragments/cart-button :: cart-button";
            }

            return "redirect:/cart";
        } catch (Exception ex) {
            logger.error("Error adding book to cart", ex);
            throw new RuntimeException("Error adding book to cart", ex);
        }
    }

    /**
     * Видаляє книгу з кошика.
     */
    @PostMapping("/remove")
    public String removeBookFromCart(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @RequestParam("bookId") Long bookId,
                                     HttpSession session,
                                     Model model) {
        try {
            Book book = bookService.getBookById(bookId);

            if (customUserDetails != null) {
                User user = customUserDetails.getUser(); // Отримуємо користувача з CustomUserDetails
                cartService.removeBookFromCart(user, book);
                model.addAttribute("cartBookIds", cartService.getCartBookIds(user));
            } else {
                List<CartBook> guestCart = getGuestCart(session);
                List<CartBook> updatedCart = cartService.removeBookFromGuestCart(guestCart, book);
                session.setAttribute("guestCart", updatedCart);
                model.addAttribute("cartBookIds", updatedCart.stream().map(c -> c.getBook().getId()).toList());
            }

            return "redirect:/cart";
        } catch (Exception ex) {
            logger.error("Error removing book from cart", ex);
            throw new RuntimeException("Error removing book from cart", ex);
        }
    }

    /**
     * Отримує гостьовий кошик із сесії або створює новий, якщо він відсутній.
     */
    private List<CartBook> getGuestCart(HttpSession session) {
        Object cartAttr = session.getAttribute("guestCart");
        if (cartAttr instanceof List<?>) {
            return (List<CartBook>) cartAttr;
        } else {
            return new ArrayList<>();
        }
    }
}
