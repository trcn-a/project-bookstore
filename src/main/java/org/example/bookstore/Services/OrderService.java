package org.example.bookstore.Services;

import jakarta.persistence.EntityNotFoundException;
import org.example.bookstore.Entities.Order;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Entities.OrderedBook;
import org.example.bookstore.Repositories.OrderRepository;
import org.example.bookstore.Repositories.OrderedBookRepository;
import org.example.bookstore.Repositories.UserRepository;
import org.example.bookstore.Repositories.BookRepository;
import org.example.bookstore.Repositories.CartBookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервісний клас для управління замовленнями користувачів.
 * Включає бізнес-логіку для створення замовлень, їх скасування,
 * отримання інформації про замовлення та товари, що входять до складу замовлення.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderedBookRepository orderedBookRepository;
    private final UserRepository userRepository;
    private final CartBookRepository cartBookRepository;
    private final BookRepository bookRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    /**
     * Конструктор, який ініціалізує OrderService усіма необхідними репозиторіями для обробки замовлень.
     *
     * @param orderRepository Репозиторій для збереження та отримання замовлень.
     * @param orderedBookRepository Репозиторій для взаємодії з книгами, що входять до замовлення.
     * @param userRepository Репозиторій для роботи з інформацією про користувачів.
     * @param cartBookRepository Репозиторій для роботи з книгами в кошику.
     * @param bookRepository Репозиторій для доступу до даних про книги.
     */
    public OrderService(OrderRepository orderRepository,
                        OrderedBookRepository orderedBookRepository,
                        UserRepository userRepository,
                        CartBookRepository cartBookRepository,
                        BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.orderedBookRepository = orderedBookRepository;
        this.userRepository = userRepository;
        this.cartBookRepository = cartBookRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Створює нове замовлення для користувача.
     *
     * @param userId            Ідентифікатор користувача, який здійснює замовлення.
     * @param phoneNumber       Номер телефону користувача.
     * @param firstName         Ім'я користувача.
     * @param lastName          Прізвище користувача.
     * @param city              Місто користувача.
     * @param postOfficeNumber  Номер поштового відділення користувача.
     * @return Замовлення, яке було створено.
     * @throws IllegalArgumentException Якщо одне з полів відсутнє або кошик порожній.
     * @throws RuntimeException Якщо користувача або кошик не знайдено.
     */
    @Transactional
    public Order createOrder(Long userId, String phoneNumber, String firstName,
                             String lastName, String city, String postOfficeNumber) {

        logger.info("Creating order for user with id={}", userId);

        if (userId == null || phoneNumber == null || phoneNumber.isBlank() ||
                firstName == null || firstName.isBlank() ||
                lastName == null || lastName.isBlank() ||
                city == null || city.isBlank() ||
                postOfficeNumber == null || postOfficeNumber.isBlank()) {
            logger.error("Creating order failed: All fields are required");
            throw new IllegalArgumentException("All fields are required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found: id={}", userId);
                    return new RuntimeException("User not found");
                });

        List<CartBook> cartBooks = cartBookRepository.findByUserIdOrderByIdAsc(user.getId());
        if (cartBooks.isEmpty()) {
            logger.error("Cart is empty for user with id={}", userId);
            throw new RuntimeException("Cart is empty");
        }

        for (CartBook cartBook : cartBooks) {
            Book book = cartBook.getBook();
            if (book.getStockQuantity() < cartBook.getQuantity()) {
                logger.error("Insufficient stock for book '{}'. Requested={}, Available={}",
                        book.getTitle(), cartBook.getQuantity(), book.getStockQuantity());
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
        order.setTotalAmount(cartBookRepository.calculateTotalSumByUserId(user.getId()));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        logger.info("Order created with id={}", order.getId());

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
        logger.info("Cart cleared for user with id={}", userId);

        return order;
    }

    /**
     * Отримує замовлення за його ідентифікатором.
     *
     * @param orderId Ідентифікатор замовлення.
     * @return Замовлення з відповідним ідентифікатором.
     * @throws RuntimeException Якщо замовлення не знайдено.
     */
    public Order getOrderById(Long orderId) {
        logger.info("Retrieving order with id={}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found: id={}", orderId);
                    return new RuntimeException("Order not found");
                });
    }

    /**
     * Отримує список замовлень користувача.
     *
     * @param userId Ідентифікатор користувача.
     * @return Список замовлень користувача.
     * @throws RuntimeException Якщо користувач не знайдений.
     */
    public List<Order> getUserOrders(Long userId) {
        logger.info("Retrieving orders for user with id={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found: id={}", userId);
                    return new RuntimeException("User not found");
                });
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    /**
     * Отримує список книг, які входять до складу конкретного замовлення.
     *
     * @param orderId Ідентифікатор замовлення.
     * @return Список книг в замовленні.
     */
    public List<OrderedBook> getOrderedBooks(Long orderId) {
        logger.info("Retrieving books for order with id={}", orderId);
        return orderedBookRepository.findByOrderId(orderId);
    }

    /**
     * Скасовує замовлення користувача.
     *
     * @param orderId Ідентифікатор замовлення.
     * @param userId  Ідентифікатор користувача, який скасовує замовлення.
     * @throws RuntimeException Якщо замовлення не знайдено, якщо користувач намагається скасувати чужі замовлення
     *                          або замовлення вже не можна скасувати.
     */
    public void cancelOrder(Long orderId, Long userId) {
        logger.info("Canceling order with id={} by user with id={}", orderId, userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found: id={}", orderId);
                    return new RuntimeException("Order not found");
                });

        if (!order.getUser().getId().equals(userId)) {
            logger.error("User with id={} attempted to cancel another user's order", userId);
            throw new RuntimeException("You can only cancel your own orders");
        }

        if (!Objects.equals(order.getStatus(), "NEW")) {
            logger.error("Order with id={} cannot be canceled because its status is '{}'", orderId, order.getStatus());
            throw new RuntimeException("Only new orders can be canceled");
        }

        order.setStatus("CANCELED");
        orderRepository.save(order);
        logger.info("Order with id={} has been canceled", orderId);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public void updateOrder(Long orderId, String status, String trackingNumber) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status);
            order.setTrackingNumber(trackingNumber);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        } else {
            throw new EntityNotFoundException("Замовлення з ID " + orderId + " не знайдено.");
        }
    }
}
