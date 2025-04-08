package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.Cart;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.BookService;
import org.example.bookstore.Services.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping
    public String viewCart(@SessionAttribute(value = "user", required = false) User user, Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }
        List<CartBook> cartBooks = cartService.getCartContents(user);
        double totalSum = cartService.getTotalSumForCart(user);

        model.addAttribute("cartBooks", cartBooks);
        model.addAttribute("totalSum", totalSum);
        return "cart";
    }

    @PostMapping("/add")
    public String addBookToCart(@SessionAttribute(value = "user", required = false) User user,
                                @RequestParam("bookId") Long bookId,
                                @RequestParam("quantity") int quantity,
                                @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                @RequestParam(required = false) String fromPage,
                                Model model) {
        if (user == null) {
            return "redirect:/user/login";
        }

        Book book = bookService.getBookById(bookId);
        cartService.addOrUpdateBookInCart(user, book, quantity);

        if ("XMLHttpRequest".equals(requestedWith)) {

            model.addAttribute("book", book);
            model.addAttribute("fromPage", fromPage);
            model.addAttribute("cartBookIds", cartService.getCartContents(user)
                    .stream()
                    .map(cartBook -> cartBook.getBook().getId())
                    .collect(Collectors.toSet()));
            return "fragments/cart-button :: cart-button";
        }

        return "redirect:" + (fromPage != null ? fromPage : "/cart");
    }

    @PostMapping("/remove")
    public String removeBookFromCart(@SessionAttribute("user") User user,
                                     @RequestParam("bookId") Long bookId,
                                     @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                                     @RequestParam(required = false) String fromPage,
                                     Model model) {
        Book book = bookService.getBookById(bookId);
        cartService.removeBookFromCart(user, book);

        if ("XMLHttpRequest".equals(requestedWith)) {
            model.addAttribute("book", book);
            model.addAttribute("fromPage", fromPage);
            model.addAttribute("cartBookIds", cartService.getCartContents(user)
                    .stream()
                    .map(cartBook -> cartBook.getBook().getId())
                    .collect(Collectors.toSet()));
            return "fragments/cart-button :: cart-button";

        }

        return "redirect:" + (fromPage != null ? fromPage : "/cart");
    }
}
