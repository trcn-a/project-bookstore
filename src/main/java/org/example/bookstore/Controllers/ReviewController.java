package org.example.bookstore.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Services.ReviewService;
import org.example.bookstore.Config.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Контролер для обробки додавання та видалення відгуків користувачів до книг.
 */
@Controller
@RequestMapping("/")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/book/{bookId}/review")
    public String addReview(
            @PathVariable Long bookId,
            @RequestParam int rating,
            @RequestParam String comment,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (customUserDetails == null) {
            throw new RuntimeException("Unauthorized attempt to add a review for book ID: " + bookId);
        }

        User user = customUserDetails.getUser();

        try {
            reviewService.addReview(bookId, user.getId(), rating, comment);
            logger.info("User ID: {} added a review for book ID: {} with rating: {}", user.getId(), bookId, rating);
            return "redirect:/book/" + bookId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add review for book ID: " + bookId, e);
        }
    }

    @PostMapping("/book/{bookId}/review/delete")
    public String deleteReview(
            @PathVariable Long bookId,
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (customUserDetails == null) {
            throw new RuntimeException("User is not authenticated.");
        }

        User user = customUserDetails.getUser();

        try {
            reviewService.deleteReview(bookId, user.getId());

            String referer = request.getHeader("Referer");

            if (referer != null && referer.contains("/reviews")) {
                return "redirect:/reviews";
            }

            return "redirect:/book/" + bookId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete review for book ID: " + bookId, e);
        }
    }

    @GetMapping("/reviews")
    public String showUserReviews(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        if (customUserDetails == null) {
            return "redirect:/login";
        }

        try {
            User user = customUserDetails.getUser();
            logger.info("Displaying reviews for user: {}", user.getEmail());

            model.addAttribute("reviews", reviewService.getUserReviews(user.getId()));
            return "user-reviews";
        } catch (IllegalStateException e) {
            logger.warn("User not authenticated, redirecting to login");
            return "redirect:/login";
        }
    }
}
