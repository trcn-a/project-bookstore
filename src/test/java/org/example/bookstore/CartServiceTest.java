package org.example.bookstore;

import org.example.bookstore.Entities.*;
import org.example.bookstore.Services.CartService;
import org.example.bookstore.repository.BookRepository;
import org.example.bookstore.repository.CartBookRepository;
import org.example.bookstore.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartBookRepository cartBookRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private Book book;
    private User user;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        this.cartService = new CartService(bookRepository, cartRepository, cartBookRepository);

        // Створення тестових об'єктів
        user = new User();
        user.setId(1L);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotalPrice(0);

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPrice(100);
        book.setStockQuantity(10);
    }

    // Тест для створення кошика
    @Test
    void testCreateCartSuccess() {
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart createdCart = cartService.createCart(user);

        assertNotNull(createdCart);
        assertEquals(user.getId(), createdCart.getUser().getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testCreateCartWhenCartExists() {

        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.of(cart));

        Cart existingCart = cartService.createCart(user);

        assertEquals(cart, existingCart);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    // Тест для додавання книги до кошика
    @Test
    void testAddBookToCartWhenBookIsAvailable() {

        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        when(cartBookRepository.findByCartIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(cartBookRepository.save(any(CartBook.class))).thenReturn(new CartBook());
        when(cartBookRepository.calculateTotalPrice(anyLong())).thenReturn(300);

        Cart updatedCart = cartService.addBookToCart(cart.getId(), book.getId(), 3);

        assertNotNull(updatedCart);
        assertEquals(300, updatedCart.getTotalPrice());
        verify(cartBookRepository, times(1)).save(any(CartBook.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    // Тест для додавання книги до кошика, якщо книги не вистачає на складі
    @Test
    void testAddBookToCartWhenBookNotInStock() {

        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                cartService.addBookToCart(cart.getId(), book.getId(), 20));

        assertEquals("Not enough stock available for the book.", exception.getMessage());
    }

    // Тест для додавання книги до кошика, коли вона вже є в кошику
    @Test
    void testAddBookToCartWhenBookAlreadyInCart() {

        CartBook cartBook = new CartBook();
        cartBook.setCart(cart);
        cartBook.setBook(book);
        cartBook.setQuantity(1);
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(cartBookRepository.findByCartIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.of(cartBook));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                cartService.addBookToCart(cart.getId(), book.getId(), 1));

        assertEquals("This book is already in the cart.", exception.getMessage());
    }

    // Тест для оновлення кількості книги в кошику
    @Test
    void testUpdateBookQuantityInCart() {

        CartBook cartBook = new CartBook();
        cartBook.setCart(cart);
        cartBook.setBook(book);
        cartBook.setQuantity(5);

        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(cartBookRepository.findByCartIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.of(cartBook));
        when(cartBookRepository.save(any(CartBook.class))).thenReturn(cartBook);
        when(cartBookRepository.calculateTotalPrice(anyLong())).thenReturn(500);

        Cart updatedCart = cartService.updateBookQuantityInCart(cart.getId(), book.getId(), 8);

        assertNotNull(updatedCart);
        assertEquals(500, updatedCart.getTotalPrice());
        assertEquals(8, cartBook.getQuantity());
        verify(cartBookRepository).save(cartBook);
        verify(cartRepository).save(any(Cart.class));
    }

    // Тест для оновлення кількості книги в кошику, якщо книги не вистачає на складі
    @Test
    void testUpdateBookQuantityInCartWhenNotEnoughStock() {
        CartBook cartBook = new CartBook();
        cartBook.setCart(cart);
        cartBook.setBook(book);
        cartBook.setQuantity(2);
        cartBook.setPricePerBook(book.getPrice());

        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(cartBookRepository.findByCartIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.of(cartBook));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateBookQuantityInCart(cart.getId(), book.getId(), 15);
        });

        assertEquals("Not enough stock available to set this quantity.", exception.getMessage());
    }

    // Тест для видалення книги з кошика
    @Test
    void testRemoveBookFromCart() {

        CartBook cartBook = new CartBook();
        cartBook.setCart(cart);
        cartBook.setBook(book);
        cartBook.setQuantity(2);
        cartBook.setPricePerBook(book.getPrice());

        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(cartBookRepository.findByCartIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.of(cartBook));
        when(cartBookRepository.calculateTotalPrice(anyLong())).thenReturn(200);

        Cart updatedCart = cartService.removeBookFromCart(cart.getId(), book.getId());

        assertNotNull(updatedCart);
        assertEquals(200, updatedCart.getTotalPrice());
        verify(cartBookRepository, times(1)).delete(any(CartBook.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}
