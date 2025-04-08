package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.Services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final BookService bookService;

    @Autowired
    public SearchController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/suggestions")
    public String getSearchSuggestions(@RequestParam String query, Model model) {
        if (query == null || query.trim().length() < 2) {
            model.addAttribute("books", List.of());
            return "fragments/search-results :: search-results";
        }

        String searchQuery = query.trim();
        List<Book> suggestions = bookService.searchBooks(searchQuery);

        if (suggestions.size() > 15) {
            suggestions = suggestions.subList(0, 5);
        }
        System.out.println(suggestions);
        model.addAttribute("searchBooks", suggestions);
        return "fragments/search-results :: search-results";
    }
}