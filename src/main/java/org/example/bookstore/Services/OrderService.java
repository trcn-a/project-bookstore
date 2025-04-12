package org.example.bookstore.Services;

import org.example.bookstore.Entities.Order;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Entities.Cart;
import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Entities.OrderedBook;
import org.example.bookstore.Repositories.OrderRepository;
import org.example.bookstore.Repositories.OrderedBookRepository;
import org.example.bookstore.Repositories.UserRepository;
import org.example.bookstore.Repositories.CartRepository;
import org.example.bookstore.Repositories.BookRepository;
import org.example.bookstore.Repositories.CartBookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    private final CartRepository cartRepository;
    private final CartBookRepository cartBookRepository;
    private final BookRepository bookRepository;

    /**
     * Конструктор, який ініціалізує OrderService усіма необхідними репозиторіями для обробки замовлень.
     *
     * @param orderRepository Репозиторій для збереження та отримання замовлень.
     * @param orderedBookRepository Репозиторій для взаємодії з книгами, що входять до замовлення.
     * @param userRepository Репозиторій для роботи з інформацією про користувачів.
     * @param cartRepository Репозиторій для взаємодії з кошиками користувачів.
     * @param cartBookRepository Репозиторій для роботи з книгами в кошику.
     * @param bookRepository Репозиторій для доступу до даних про книги.
     */
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

    /**
     * Отримує замовлення за його ідентифікатором.
     *
     * @param orderId Ідентифікатор замовлення.
     * @return Замовлення з відповідним ідентифікатором.
     * @throws RuntimeException Якщо замовлення не знайдено.
     */
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    /**
     * Отримує список замовлень користувача.
     *
     * @param userId Ідентифікатор користувача.
     * @return Список замовлень користувача.
     * @throws RuntimeException Якщо користувач не знайдений.
     */
    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    /**
     * Отримує список книг, які входять до складу конкретного замовлення.
     *
     * @param orderId Ідентифікатор замовлення.
     * @return Список книг в замовленні.
     */
    public List<OrderedBook> getOrderedBooks(Long orderId) {
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
