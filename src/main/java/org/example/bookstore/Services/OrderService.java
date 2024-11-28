package org.example.bookstore.Services;


import org.example.bookstore.Entities.*;
import org.example.bookstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderedBookRepository orderedBookRepository;
    private final CartRepository cartRepository;
    private final CartBookRepository cartBookRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderedBookRepository orderedBookRepository,
                        CartRepository cartRepository,
                        CartBookRepository cartBookRepository) {
        this.orderRepository = orderRepository;
        this.orderedBookRepository = orderedBookRepository;
        this.cartRepository = cartRepository;
        this.cartBookRepository = cartBookRepository;
    }

    // Створення нового замовлення
    public Order createOrder(User user, String phoneNumber, String recipientName,
                             String deliveryAddress, String paymentStatus) {

        // Перевірка наявності кошика
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getTotalPrice() <= 0) {
            throw new RuntimeException("Cart is empty.");
        }

        // Створення нового замовлення
        Order order = new Order();
        order.setUser(user);
        order.setPhoneNumber(phoneNumber);
        order.setRecipientName(recipientName);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus("NEW");
        order.setTotalAmount(cart.getTotalPrice());
        order.setPaymentStatus(paymentStatus);
        order.setCreatedAt(LocalDateTime.now());

        // Додавання книг до замовлення
        List<CartBook> cartBooks = cartBookRepository.findByCartId(cart.getId());
        for (CartBook cartBook : cartBooks) {
            OrderedBook orderedBook = new OrderedBook();
            orderedBook.setOrder(order);
            orderedBook.setBook(cartBook.getBook());
            orderedBook.setQuantity(cartBook.getQuantity());
            orderedBook.setPricePerBook(cartBook.getPricePerBook());
            orderedBookRepository.save(orderedBook);
        }

        // Збереження замовлення
        return orderRepository.save(order);
    }

    // Отримання замовлення за ID
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }


    // Оновлення статусу замовлення
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // Оновлення номера для відстежування доставки
    public Order updateTrackingNumber(Long orderId, String trackingNumber) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setTrackingNumber(trackingNumber);
        return orderRepository.save(order);
    }

    // Видалення замовлення
    public void deleteOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderRepository.delete(order);
    }
}
