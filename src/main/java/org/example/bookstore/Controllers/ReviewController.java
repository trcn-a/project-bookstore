package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Контролер для обробки додавання та видалення відгуків користувачів до книг.
 */
@Controller
@RequestMapping("/book")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    /**
     * Конструктор контролера, який приймає сервіс відгуків через інʼєкцію залежності.
     *
     * @param reviewService сервіс для роботи з відгуками
     */
    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Обробляє POST-запит на додавання відгуку до конкретної книги.
     *
     * @param bookId  ідентифікатор книги
     * @param rating  рейтинг, поставлений користувачем
     * @param comment текстовий коментар користувача
     * @param session HTTP-сесія для отримання авторизованого користувача
     * @return перенаправлення на сторінку книги
     * @throws IllegalStateException якщо користувач не авторизований
     */
    @PostMapping("/{bookId}/review")
    public String addReview(
            @PathVariable Long bookId,
            @RequestParam int rating,
            @RequestParam String comment,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("Unauthorized attempt to add a review for book ID: " + bookId);

        }

        try {
            reviewService.addReview(bookId, user.getId(), rating, comment);
            logger.info("User ID: {} added a review for book ID: {} with rating: {}", user.getId(), bookId, rating);
            return "redirect:/book/" + bookId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add review for book ID: " + bookId, e);

        }
    }

    /**
     * Обробляє POST-запит на видалення відгуку користувача для конкретної книги.
     *
     * @param bookId  ідентифікатор книги
     * @param request HTTP-запит для перевірки сторінки-напрямку
     * @param session HTTP-сесія для отримання авторизованого користувача
     * @return перенаправлення на сторінку відгуків користувача або на сторінку книги
     * @throws IllegalStateException якщо користувач не авторизований
     */
    @PostMapping("/{bookId}/review/delete")
    public String deleteReview(
            @PathVariable Long bookId,
            HttpServletRequest request,
            HttpSession session) {

        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new RuntimeException("User is not authenticated.");
            }

            reviewService.deleteReview(bookId, user.getId());

            String referer = request.getHeader("Referer");

            if (referer != null && referer.contains("/user/reviews")) {
                return "redirect:/user/reviews";
            }

            return "redirect:/book/" + bookId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete review for book ID: " + bookId, e);
        }
    }
}
