package org.example.bookstore.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

/**
 * Сутність, що представляє зв'язок між користувачем та книгою для кошика.
 * Визначає, яка книга додана до кошика користувача та в якій кількості.
 */
@Entity
@Table(name = "cart_books", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
@Check(constraints = "quantity > 0 and quantity <= 10")
public class CartBook {

    /**
     * Унікальний ідентифікатор запису книги у кошику. Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Користувач, який додав книгу до кошика
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Книга, що була додана до кошика.
     */
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    /**
     * Кількість доданих книг одного виду до кошика.
     */
   @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Повертає користувача, якому належить кошик
     *
     * @return користувач
     */
    public User getUser() {
        return user;
    }


    /**
     * Встановлює користувача, якому належить кошик.
     *
     * @param user користувач
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Повертає книгу, що міститься у кошику.
     *
     * @return об'єкт книги
     */
    public Book getBook() {
        return book;
    }

    /**
     * Встановлює книгу, що додається до кошика.
     *
     * @param book об'єкт книги
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Повертає кількість екземплярів книги в кошику.
     *
     * @return кількість книг
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Встановлює кількість екземплярів книги в кошику.
     *
     * @param quantity кількість книг
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }



}
