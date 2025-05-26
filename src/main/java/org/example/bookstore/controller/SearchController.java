package org.example.bookstore.controller;

import org.example.bookstore.entity.Book;
import org.example.bookstore.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Контролер для обробки пошукових запитів та надання пропозицій за результатами пошуку.
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private final BookService bookService;

    /**
     * Конструктор контролера, який приймає сервіс для роботи з книгами через інʼєкцію залежності.
     *
     * @param bookService сервіс для роботи з книгами
     */
    @Autowired
    public SearchController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Обробляє запити на отримання пропозицій за введеним пошуковим запитом.
     * Пошук здійснюється за книгами, які відповідають введеному тексту.
     *
     * @param query рядок пошукового запиту
     * @param model модель для передачі атрибутів в представлення
     * @return фрагмент з результатами пошуку
     */
    @GetMapping("/suggestions")
    public String getSearchSuggestions(@RequestParam String query, Model model) {
        try {
            if (query == null || query.trim().length() < 2) {
                model.addAttribute("books", List.of());
                return "fragments/search-results :: search-results";
            }

            String searchQuery = query.trim();
            List<Book> suggestions = bookService.searchBooks(searchQuery);

            if (suggestions.size() > 5) {
                suggestions = suggestions.subList(0, 5);
            }

            logger.info("Search query: '{}', found {} suggestions.", searchQuery, suggestions.size());

            model.addAttribute("searchBooks", suggestions);
            return "fragments/search-results :: search-results";

        } catch (Exception e) {
            throw new RuntimeException("Failed to process search suggestions for query: '" + query + "'", e);
        }
    }
}
