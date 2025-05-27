package org.example.bookstore.controller;

import org.example.bookstore.entity.Review;
import org.example.bookstore.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;


/**
 * Контролер для адміністрування відгуків у системі.
 */
@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    /**
     * Конструктор контролера, що ініціалізує залежність від ReviewService.
     *
     * @param reviewService сервіс для роботи з відгуками.
     */
    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    /**
     * Обробляє GET-запит для перегляду всіх відгуків.
     *
     * @param model об'єкт  Model для передачі даних до подання.
     * @return назва шаблону HTML для перегляду відгуків.
     */
    @GetMapping
    public String getAllReviews(Model model) {
        List<Review> reviews = reviewService.findAllReviews();
        model.addAttribute("reviews", reviews);
        return "admin-reviews";
    }

    /**
     * Обробляє POST-запит для видалення відгука за його ID.
     *
     * @param id ідентифікатор відгука, яку потрібно видалити.
     * @return редірект на сторінку списку всіх відгуків після видалення.
     */
    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReviewById(id);
        return "redirect:/admin/reviews";
    }

    /**
     * Обробляє GET-запит для пошуку відгуків за назвою книги.
     *
     * @param title назва книги, за якою виконується пошук.
     * @param model об'єкт Model для передачі результатів пошуку до подання.
     * @return назва шаблону HTML з результатами пошуку.
     */
    @GetMapping("/search")
    public String searchReviewsByBookTitle(@RequestParam("title") String title, Model model) {
        List<Review> reviews = reviewService.findReviewsByBookTitle(title);
        model.addAttribute("reviews", reviews);
        model.addAttribute("title", title);
        return "admin-reviews";
    }


}
