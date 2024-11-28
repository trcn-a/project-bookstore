package org.example.bookstore.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ordered_books")
public class OrderedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Book book;

    private Integer quantity;
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
}
