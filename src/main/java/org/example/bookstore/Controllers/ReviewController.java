package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/book")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{bookId}/review")
    public String addReview(
            @PathVariable Long bookId,
            @RequestParam int rating,
            @RequestParam String comment,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("Користувач не авторизований");
        }

        reviewService.addReview(bookId, user.getId(), rating, comment);
        return "redirect:/book/" + bookId;


    }

    @PostMapping("/{bookId}/review/delete")
    public String deleteReview(
            @PathVariable Long bookId,
            HttpServletRequest request,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("Користувач не авторизований");
        }

        reviewService.deleteReview(bookId, user.getId());

        String referer = request.getHeader("Referer");

        if (referer != null && referer.contains("/user/reviews")) {
            return "redirect:/user/reviews";
        }

        return "redirect:/book/" + bookId;
    }
}
