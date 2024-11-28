package org.example.bookstore;

import org.example.bookstore.Entities.*;
import org.example.bookstore.Services.CartService;
import org.example.bookstore.Services.OrderService;
import org.example.bookstore.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderedBookRepository orderedBookRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartBookRepository cartBookRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private CartBook cartBook;
    private Book book;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.orderService = new OrderService(orderRepository, orderedBookRepository, cartRepository, cartBookRepository);

        user = new User();
        user.setId(1L);

        book = new Book();
        book.setId(1L);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotalPrice(100);

        cartBook = new CartBook();
        cartBook.setBook(book);
        cartBook.setQuantity(1);
        cartBook.setPricePerBook(100);
        cartBook.setCart(cart);

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setPhoneNumber("123456789");
        order.setRecipientName("John Doe");
        order.setDeliveryAddress("Some Address");
        order.setStatus("NEW");
        order.setTotalAmount(100);
        order.setPaymentStatus("PAID");
        order.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createOrderWhenCartNotFound() {
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> orderService.createOrder(user, "123456789", "John Doe", "Some Address", "PAID"));
        assertEquals("Cart not found", thrown.getMessage());
    }

    @Test
    void createOrderWhenCartIsEmpty() {

        cart.setTotalPrice(0);
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> orderService.createOrder(user, "123456789", "John Doe", "Some Address", "PAID"));
        assertEquals("Cart is empty.", thrown.getMessage());
    }

    @Test
    void createOrderSuccess() {

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartBookRepository.findByCartId(cart.getId())).thenReturn(Collections.singletonList(cartBook));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderedBookRepository.save(any(OrderedBook.class))).thenReturn(new OrderedBook());  // Мокуємо save

        Order createdOrder = orderService.createOrder(user, "123456789", "John Doe", "Some Address", "PAID");

        assertNotNull(createdOrder);
        assertEquals("NEW", createdOrder.getStatus());
        assertEquals(100, createdOrder.getTotalAmount());
        assertEquals("PAID", createdOrder.getPaymentStatus());
        assertNotNull(createdOrder.getCreatedAt());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderedBookRepository, times(1)).save(any(OrderedBook.class));
    }

}
