package org.example.bookstore.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ordered_books")
public class OrderedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name="book_id")
    private Book book;

    private Integer quantity;
    @JoinColumn(name="price_per_book")

    private Integer pricePerBook;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPricePerBook() {
        return pricePerBook;
    }

    public void setPricePerBook(Integer pricePerBook) {
        this.pricePerBook = pricePerBook;
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }
}
