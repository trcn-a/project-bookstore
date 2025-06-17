package org.example.bookstore.controller;

import jakarta.servlet.http.HttpSession;
import org.example.bookstore.config.CustomUserDetails;
import org.example.bookstore.entity.*;
import org.example.bookstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Контролер для обробки запитів, пов'язаних з кошиком
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final BookService bookService;

    @Autowired
    public CartController(CartService cartService, BookService bookService) {
        this.cartService = cartService;
        this.bookService = bookService;
    }

    /**
     * Відображає вміст кошика
     *
     * @param customUserDetails об'єкт, що містить дані авторизованого користувача
     * @param session           сесія HTTP для зберігання гостьового кошика
     * @param model             модель для передачі атрибутів у шаблон
     * @return ім'я шаблону для відображення кошика
     */
    @GetMapping
    public String viewCart(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                           HttpSession session, Model model) {
        try {
            List<CartBook> cartBooks;
            double totalSum;
            List<String> removedBooks;

            if (customUserDetails != null) {
                User user = customUserDetails.getUser();
                removedBooks = cartService.checkAndCleanCart(user);
                cartBooks = cartService.getCartContents(user);
                totalSum = cartService.getTotalSumForCart(user);
            } else {
                List<CartBook> guestCart = getGuestCart(session);
                removedBooks = new ArrayList<>();
                List<String> finalRemovedBooks = removedBooks;
                guestCart.removeIf(cartBook -> {
                    Book book = cartBook.getBook();
                    boolean remove = book.getStockQuantity() < cartBook.getQuantity() || book.getStockQuantity() <= 0;
                    if (remove) finalRemovedBooks.add(book.getTitle());
                    return remove;
                });

                cartBooks = guestCart;
                totalSum = cartService.getTotalSumForGuestCart(guestCart);
                session.setAttribute("guestCart", guestCart);
            }

            model.addAttribute("cartBooks", cartBooks);
            model.addAttribute("totalSum", totalSum);

            if (!removedBooks.isEmpty()) {
                model.addAttribute("removedBooksMessage", "Деякі книги були видалені з кошика через відсутність у наявності: "
                        + String.join(", ", removedBooks));
            }

            return "cart";
        } catch (Exception ex) {
            throw new RuntimeException("Error loading cart", ex);
        }
    }

    /**
     * Додає або змінює кількість книги в кошику
     *
     * @param customUserDetails об'єкт, що містить деталі авторизованого користувача
     * @param bookId            ідентифікатор книги
     * @param quantity          кількість примірників книги
     * @param requestedWith     заголовок, що вказує на тип запиту (XMLHttpRequest для AJAX)
     * @param session           сесія HTTP для зберігання гостьового кошика
     * @param model             модель для передачі атрибутів у шаблон
     * @return фрагмент для AJAX або перенаправлення на сторінку кошика
     */
    @PostMapping("/add")
    public String addBookToCart(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                @RequestParam("bookId") Long bookId, @RequestParam("quantity") int quantity,
                                @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                HttpSession session, Model model) {
        try {
            Book book = bookService.getBookById(bookId);
            int cartQuantity;
            if (customUserDetails != null) {
                User user = customUserDetails.getUser();
                cartService.addOrUpdateBookInCart(user, book, quantity);
                model.addAttribute("cartBookIds", cartService.getCartBookIds(user));
                cartQuantity = cartService.getTotalQuantityForUserCart(user);
            } else {
                List<CartBook> guestCart = getGuestCart(session);
                guestCart = cartService.addOrUpdateBookInGuestCart(guestCart, book, quantity);
                session.setAttribute("guestCart", guestCart);
                model.addAttribute("cartBookIds", guestCart.stream().map(c -> c.getBook().getId()).toList());
                cartQuantity = cartService.getTotalQuantityForGuestCart(guestCart);
            }
            session.setAttribute("cartQuantity", cartQuantity);
            if ("XMLHttpRequest".equals(requestedWith)) {
                model.addAttribute("book", book);
                return "fragments/cart-button :: cart-button";
            }

            return "redirect:/cart";
        } catch (Exception ex) {
            throw new RuntimeException("Error adding book to cart", ex);
        }
    }

    /**
     * Видаляю книгу з кошика
     *
     * @param customUserDetails об'єкт, що містить деталі авторизованого користувача
     * @param bookId            ідентифікатор книги
     * @param session           сесія HTTP для зберігання гостьового кошика
     * @param model             модель для передачі атрибутів у шаблон
     * @return перенаправлення на сторінку кошика
     */
    @PostMapping("/remove")
    public String removeBookFromCart(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @RequestParam("bookId") Long bookId,
                                     HttpSession session, Model model) {
        try {
            Book book = bookService.getBookById(bookId);
            int cartQuantity;

            if (customUserDetails != null) {
                User user = customUserDetails.getUser();
                cartService.removeBookFromCart(user, book);
                model.addAttribute("cartBookIds", cartService.getCartBookIds(user));
                cartQuantity = cartService.getTotalQuantityForUserCart(user);
            } else {
                List<CartBook> guestCart = getGuestCart(session);
                List<CartBook> updatedCart = cartService.removeBookFromGuestCart(guestCart, book);
                session.setAttribute("guestCart", updatedCart);
                model.addAttribute("cartBookIds", updatedCart.stream().map(c -> c.getBook().getId()).toList());
                cartQuantity = cartService.getTotalQuantityForGuestCart(guestCart);
            }
            session.setAttribute("cartQuantity", cartQuantity);

            return "redirect:/cart";
        } catch (Exception ex) {
            throw new RuntimeException("Error removing book from cart", ex);
        }
    }

    /**
     * Отримує дані гостьового кошика
     *
     * @param session сесія HTTP
     * @return список книг у гостьовому кошику
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
