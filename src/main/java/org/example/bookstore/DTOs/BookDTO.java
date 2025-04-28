package org.example.bookstore.DTOs;

import org.example.bookstore.Entities.Author;
import org.example.bookstore.Entities.Book;

/**
 * DTO для відображення інформації про книгу
 */
public class BookDTO {
    private Long id;
    private String title;
    private Author author;
    private String genreName;
    private Integer originalPrice;
    private Integer actualPrice;
    private String coverImage;
    private String description;
    private Integer stockQuantity;


    public BookDTO(Book book, Integer actualPrice) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.genreName = book.getGenre() != null ? book.getGenre().getName() : null;
        this.originalPrice = book.getPrice();
        this.actualPrice = actualPrice;
        this.coverImage = book.getCoverImage();
        this.description = book.getDescription();
        this.stockQuantity = book.getStockQuantity();
    }

    // Геттери
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public String getGenreName() {
        return genreName;
    }

    public Integer getOriginalPrice() {
        return originalPrice;
    }

    public Integer getActualPrice() {
        return actualPrice;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getDescription() {
        return description;
    }
    public Integer getStockQuantity() {
        return stockQuantity;
    }
}