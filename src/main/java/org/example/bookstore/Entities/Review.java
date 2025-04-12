package org.example.bookstore.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

/**
 * Сутність, що представляє відгук користувача про книгу.
 * Містить інформацію про рейтинг, текст відгука та час створення відгука.
 */
@Entity
@Table(name = "reviews")
public class Review {

    /**
     * Унікальний ідентифікатор відгука.
     * Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Користувач, який написав відгук.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Книга, на яку написаний відгук.
     */
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    /**
     * Рейтинг книги, який поставив користувач (від 1 до 5).
     */
    private Integer rating;

    /**
     * Текст відгука користувача про книгу.
     */
    @Column(name = "review_text")
    private String reviewText;

    /**
     * Час створення відгука.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Повертає унікальний ідентифікатор відгука.
     *
     * @return ідентифікатор відгука
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор відгука.
     *
     * @param id ідентифікатор відгука
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає користувача, який написав цей відгук.
     *
     * @return об'єкт користувача
     */
    public User getUser() {
        return user;
    }

    /**
     * Встановлює користувача, який написав цей відгук.
     *
     * @param user об'єкт користувача
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Повертає книгу, на яку написаний відгук.
     *
     * @return об'єкт книги
     */
    public Book getBook() {
        return book;
    }

    /**
     * Встановлює книгу, на яку написаний відгук.
     *
     * @param book об'єкт книги
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Повертає рейтинг книги, який поставив користувач.
     *
     * @return рейтинг книги
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Встановлює рейтинг книги, який поставив користувач.
     *
     * @param rating рейтинг книги
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    /**
     * Повертає текст відгука користувача про книгу.
     *
     * @return текст відгука
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Встановлює текст відгука користувача про книгу.
     *
     * @param reviewText текст відгука
     */
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    /**
     * Повертає час створення відгука.
     *
     * @return час створення відгука
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Встановлює час створення відгука.
     *
     * @param createdAt час створення відгука
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
