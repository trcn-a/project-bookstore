package org.example.bookstore.Entities;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_books")
public class CartBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Book book;

    private Integer quantity;
    private Integer pricePerBook;

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
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

    // Getters and Setters

}