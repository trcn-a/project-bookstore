package org.example.bookstore.Services;

import org.example.bookstore.DTOs.BookDTO;
import org.example.bookstore.Entities.Book;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервіс для розрахунку цін книг
 */
@Service
public class PriceCalculationService {

    /**
     * Розраховує актуальну ціну книги з урахуванням знижки
     * 
     * @param book книга, для якої розраховується ціна
     * @return актуальна ціна з урахуванням знижки
     */
    public Integer calculateActualPrice(Book book) {
        if (book.getDiscount() == null) {
            return book.getPrice();
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(book.getDiscount().getStartDate()) || 
            now.isAfter(book.getDiscount().getEndDate())) {
            return book.getPrice();
        }

        return book.getPrice() - (book.getPrice() * book.getDiscount().getDiscountPercentage() / 100);
    }

    /**
     * Конвертує список книг у список DTO з актуальними цінами
     * 
     * @param books список книг
     * @return список DTO з актуальними цінами
     */
    public List<BookDTO> convertToBookDTOs(List<Book> books) {
        return books.stream()
                .map(book -> new BookDTO(book, calculateActualPrice(book)))
                .collect(Collectors.toList());
    }


    /**
     * Конвертує одну книгу у DTO з актуальною ціною
     *
     * @param book книга
     * @return DTO книги
     */
    public BookDTO convertToBookDTO(Book book) {
        return new BookDTO(book, calculateActualPrice(book));
    }

} 