package org.example.bookstore.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;

/**
 * Сутність, що представляє зв'язок між кошиком та книгою.
 * Визначає, яка книга додана до певного кошика та в якій кількості.
 */
@Entity
@Table(name = "cart_books")
public class CartBook {

    /**
     * Унікальний ідентифікатор запису книги у кошику. Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Кошик, до якого належить книга.
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
     * Кількість доданих книг до кошика.
     */
    private Integer quantity;

    /**
     * Повертає кошик, у якому зберігається книга.
     *
     * @return об'єкт кошика
     */
    public User getUser() {
        return user;
    }

    /**
     * Встановлює кошик, у який додається книга.
     *
     * @param cart об'єкт кошика
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
