package org.example.bookstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;

/**
 * Сутність, що представляє книгу в замовленні.
 * Містить інформацію про замовлення, книгу, кількість та ціну за одиницю.
 */
@Entity
@Table(name = "ordered_books")
public class OrderedBook {

    /**
     * Унікальний ідентифікатор запису.
     * Генерується автоматично.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Замовлення, до якого належить ця книга.
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * Книга, яку було замовлено.
     */
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    /**
     * Кількість замовлених одиниць цієї книги.
     */
    private Integer quantity;

    /**
     * Ціна за одиницю книги в замовленні.
     */
    @JoinColumn(name = "price_per_book")
    private Integer pricePerBook;

    /**
     * Повертає унікальний ідентифікатор цього запису.
     *
     * @return ідентифікатор запису
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор цього запису.
     *
     * @param id ідентифікатор запису
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Повертає замовлення, до якого належить ця книга.
     *
     * @return об'єкт замовлення
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Встановлює замовлення, до якого належить ця книга.
     *
     * @param order об'єкт замовлення
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Повертає книгу, яку було замовлено.
     *
     * @return об'єкт книги
     */
    public Book getBook() {
        return book;
    }

    /**
     * Встановлює книгу, яку було замовлено.
     *
     * @param book об'єкт книги
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Повертає кількість замовлених одиниць цієї книги.
     *
     * @return кількість замовлених одиниць
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Встановлює кількість замовлених одиниць цієї книги.
     *
     * @param quantity кількість замовлених одиниць
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Повертає ціну за одиницю книги в замовленні.
     *
     * @return ціна за одиницю книги
     */
    public Integer getPricePerBook() {
        return pricePerBook;
    }

    /**
     * Встановлює ціну за одиницю книги в замовленні.
     *
     * @param pricePerBook ціна за одиницю книги
     */
    public void setPricePerBook(Integer pricePerBook) {
        this.pricePerBook = pricePerBook;
    }
}
