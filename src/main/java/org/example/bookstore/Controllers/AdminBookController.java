package org.example.bookstore.Controllers;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.Services.AuthorService;
import org.example.bookstore.Services.BookService;
import org.example.bookstore.Services.GenreService;
import org.example.bookstore.Services.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/books")
public class AdminBookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final PublisherService publisherService;

    @Autowired
    public AdminBookController(BookService bookService,
                               AuthorService authorService,
                               GenreService genreService,
                               PublisherService publisherService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
        this.publisherService = publisherService;
    }

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "admin-books";
    }

    @GetMapping({ "/create", "/edit/{id}"})
    public String showForm(@PathVariable(required = false) Long id, Model model) {
        Book book = (id != null) ? bookService.getBookById(id) : new Book();
        model.addAttribute("book", book);
        model.addAttribute("actionUrl", "/admin/books/save");
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("publishers", publisherService.findAll());
        return "fragments/book-details :: details";
    }




    @PostMapping("/save")
    public String saveBook(@RequestParam(required = false) Long id,
                           @RequestParam String title,
                           @RequestParam Integer price,
                           @RequestParam(required = false, defaultValue = "0") Integer discount,
                           @RequestParam String isbn,
                           @RequestParam Integer stockQuantity,
                           @RequestParam Integer publicationYear,
                           @RequestParam String bookFormat,
                           @RequestParam String coverType,
                           @RequestParam String authorName,
                           @RequestParam String genreName,
                           @RequestParam String publisherName,
                           @RequestParam("coverImageFile") MultipartFile coverImageFile,
                           RedirectAttributes redirectAttributes) {

        Book savedBook = bookService.saveBook(id, title, price, discount, isbn, stockQuantity,
                publicationYear, bookFormat, coverType, authorName, genreName, publisherName, coverImageFile);

        redirectAttributes.addFlashAttribute("updatedBookId", savedBook.getId());

        return "redirect:/admin/books";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam("query") String query, Model model) {
        List<Book> books = bookService.searchBooks(query);
        model.addAttribute("books", books);
        model.addAttribute("query", query);
        return "admin-books";
    }


}
