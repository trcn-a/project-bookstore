package org.example.bookstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.UniqueConstraint;

/**
 * Сутність, що представляє книгу, додану до списку обраних користувача.
 * Містить посилання на користувача та книгу.
 * Унікальність забезпечується комбінацією користувача та книги.
 */
@Entity
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
public class Favorite {

    /**
     * Унікальний ідентифікатор запису в списку обраних книг. Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Користувач, який додав книгу до списку обраних товарів.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Книга, яка була додана до списку обраних товарів.
     */
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Конструктор без параметрів.
     */
    public Favorite() { }

    /**
     * Конструктор для створення об'єкта, що представляє книгу у списку обраних товарів.
     *
     * @param user об'єкт користувача
     * @param book об'єкт книги
     */
    public Favorite(User user, Book book) {
        this.user = user;
        this.book = book;
    }

    /**
     * Повертає унікальний ідентифікатор запису в списку обраних товарів.
     *
     * @return ідентифікатор запису
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор запису в списку обраних товарів.
     *
     * @param id ідентифікатор запису
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає користувача, який додав книгу до списку обраних товарів.
     *
     * @return об'єкт користувача
     */
    public User getUser() {
        return user;
    }

    /**
     * Встановлює користувача, який додав книгу до списку обраних товарів.
     *
     * @param user об'єкт користувача
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Повертає книгу, яка була додана до списку обраних товарів.
     *
     * @return об'єкт книги
     */
    public Book getBook() {
        return book;
    }

    /**
     * Встановлює книгу для додавання до списку обраних товарів.
     *
     * @param book об'єкт книги
     */
    public void setBook(Book book) {
        this.book = book;
    }
}
