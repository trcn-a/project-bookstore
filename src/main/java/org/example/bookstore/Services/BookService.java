package org.example.bookstore.Services;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Перегляд каталогу
    public Page<Book> getBooks(int page, int size) {
        return bookRepository.findAll(PageRequest.of(page, size));
    }

    // Пошук книг
    public List<Book> searchBooks(String searchQuery) {
        return bookRepository.searchBooks(searchQuery);
    }

    // Сортування книг
    public Page<Book> getSortedBooks(String sortBy, boolean ascending, int page, int size) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        return bookRepository.findAll(PageRequest.of(page, size, Sort.by(direction, sortBy)));
    }

    // Фільтрування книг за жанром, видавцем та ціною
    public List<Book> filterBooks(List<String> authors, List<String> genres, List<String> publishers, Integer minPrice, Integer maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Min price cannot be greater than max price.");
        }
        return bookRepository.filterBooks(authors, genres, publishers, minPrice, maxPrice);
    }
}
