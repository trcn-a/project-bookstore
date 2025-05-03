package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.Review;
import org.example.bookstore.Services.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;



@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @GetMapping
    public String getAllReviews(Model model) {
        List<Review> reviews = reviewService.findAllReviews();
        model.addAttribute("reviews", reviews);
        return "admin-reviews";
    }


    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReviewById(id);
        return "redirect:/admin/reviews";
    }

    @GetMapping("/search")
    public String searchReviewsByBookTitle(@RequestParam("title") String title, Model model) {
        List<Review> reviews = reviewService.findReviewsByBookTitle(title);
        model.addAttribute("reviews", reviews);
        model.addAttribute("title", title);
        return "admin-reviews";
    }


}
