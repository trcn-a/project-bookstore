package org.example.bookstore.Services;

import org.example.bookstore.Entities.*;
import org.example.bookstore.Repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderedBookRepository orderedBookRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartBookRepository cartBookRepository;
    private final BookRepository bookRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderedBookRepository orderedBookRepository,
                        UserRepository userRepository,
                        CartRepository cartRepository,
                        CartBookRepository cartBookRepository,
                        BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.orderedBookRepository = orderedBookRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartBookRepository = cartBookRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Order createOrder(Long userId, String phoneNumber, String firstName, String lastName, String city, String postOfficeNumber) {
        if (userId == null || phoneNumber == null || phoneNumber.isBlank() ||
                firstName == null || firstName.isBlank() ||
                lastName == null || lastName.isBlank() ||
                city == null || city.isBlank() ||
                postOfficeNumber == null || postOfficeNumber.isBlank()) {
            throw new IllegalArgumentException("All fields are required");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartBook> cartBooks = cartBookRepository.findByCartId(cart.getId());
        if (cartBooks.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for (CartBook cartBook : cartBooks) {
            Book book = cartBook.getBook();
            if (book.getStockQuantity() < cartBook.getQuantity()) {
                throw new RuntimeException("Not enough stock for book: " + book.getTitle());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setPhoneNumber(phoneNumber);
        order.setFirstName(firstName);
        order.setLastName(lastName);
        order.setCity(city);
        order.setPostOfficeNumber(postOfficeNumber);
        order.setStatus("NEW");
        order.setTotalAmount(cartBookRepository.calculateTotalSumByCartId(cart.getId()));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        for (CartBook cartBook : cartBooks) {
            Book book = cartBook.getBook();
            OrderedBook orderedBook = new OrderedBook();
            orderedBook.setOrder(order);
            orderedBook.setBook(book);
            orderedBook.setQuantity(cartBook.getQuantity());
            orderedBook.setPricePerBook(book.getActualPrice());
            orderedBookRepository.save(orderedBook);

            book.setStockQuantity(book.getStockQuantity() - cartBook.getQuantity());
            bookRepository.save(book);
        }

        cartBookRepository.deleteAll(cartBooks);

        return order;
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<OrderedBook> getOrderedBooks(Long orderId) {

     return orderedBookRepository.findByOrderId(orderId);
    }

    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own orders");
        }

        if (!Objects.equals(order.getStatus(), "NEW")) {
            throw new RuntimeException("Only new orders can be cancelled");
        }

        order.setStatus("СКАСОВАНО");
        orderRepository.save(order);
    }

}
