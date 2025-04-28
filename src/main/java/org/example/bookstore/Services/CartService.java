package org.example.bookstore.Services;

import org.example.bookstore.DTOs.BookDTO;
import org.example.bookstore.Entities.Cart;
import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Repositories.CartBookRepository;
import org.example.bookstore.Repositories.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервісний клас для управління кошиком користувача.
 * Включає бізнес-логіку для додавання/оновлення книг у кошик, видалення книг,
 * отримання вмісту кошика та обчислення загальної суми кошика.
 */
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartBookRepository cartBookRepository;
    private final PriceCalculationService priceCalculationService;
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    /**
     * Конструктор, який ініціалізує CartService за допомогою переданих сервісів та репозиторіїв.
     *
     * @param cartRepository Репозиторій для взаємодії з кошиками користувачів у базі даних.
     * @param cartBookRepository Репозиторій для взаємодії з книжками в кошику.
     * @param priceCalculationService Сервіс для розрахунку цін.
     */
    public CartService(CartRepository cartRepository, 
                      CartBookRepository cartBookRepository,
                      PriceCalculationService priceCalculationService) {
        this.cartRepository = cartRepository;
        this.cartBookRepository = cartBookRepository;
        this.priceCalculationService = priceCalculationService;
    }

    /**
     * Отримує кошик користувача або створює новий, якщо він не існує.
     *
     * @param user Користувач, для якого необхідно отримати або створити кошик.
     * @return Кошик користувача.
     * @throws IllegalArgumentException Якщо користувач не авторизований.
     */
    private Cart getOrCreateCart(User user) {
        if (user == null) {
            logger.error("User is not authorized.");
            throw new IllegalArgumentException("User is not authorized.");
        }
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    logger.info("Created a new cart for user with ID: {}", user.getId());
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Додає книгу до кошика або оновлює її кількість.
     *
     * @param user Користувач, який додає книгу.
     * @param book Книга, яку потрібно додати.
     * @param quantity Кількість книг, яку додають у кошик.
     * @throws IllegalArgumentException Якщо користувач не авторизований, якщо книги немає на складі
     *                                   або кількість перевищує дозволену.
     */
    @Transactional
    public void addOrUpdateBookInCart(User user, Book book, int quantity) {
        if (user == null) {
            logger.error("User is not authorized.");
            throw new IllegalArgumentException("User is not authorized.");
        }
        if (book.getStockQuantity() <= 0) {
            logger.error("The book is currently out of stock: {}", book.getTitle());
            throw new IllegalArgumentException("The book is currently out of stock.");
        }

        // Обмеження кількості книг в кошику — не більше 10 одиниць
        if (quantity > 10) {
            logger.error("Quantity exceeds the limit. The maximum number of books in one order is 10.");
            throw new IllegalArgumentException("The maximum number of books in one order is 10.");
        }

        // Перевірка на те, чи кількість, яку додає користувач, не перевищує наявну кількість на складі
        if (quantity > book.getStockQuantity()) {
            logger.error("Not enough books in stock for book: {}. Needed: {}, available in stock: {}",
                    book.getTitle(), quantity, book.getStockQuantity());
            throw new IllegalArgumentException("Not enough books in stock.");
        }

        Cart cart = getOrCreateCart(user);

        CartBook cartBook = cartBookRepository.findByCartIdAndBookId(cart.getId(), book.getId())
                .orElseGet(() -> {
                    CartBook newCartBook = new CartBook();
                    newCartBook.setCart(cart);
                    newCartBook.setBook(book);
                    return newCartBook;
                });

        cartBook.setQuantity(quantity);
        cartBookRepository.save(cartBook);
        logger.info("Book '{}' added to the cart of user with ID: {}. Quantity: {}",
                book.getTitle(), user.getId(), quantity);
    }

    /**
     * Видаляє книгу з кошика користувача.
     *
     * @param user Користувач, який видаляє книгу.
     * @param book Книга, яку потрібно видалити з кошика.
     * @throws IllegalArgumentException Якщо книга відсутня в кошику або користувач не авторизований.
     */
    @Transactional
    public void removeBookFromCart(User user, Book book) {
        if (user == null) {
            logger.error("User is not authorized.");
            throw new IllegalArgumentException("User is not authorized.");
        }
        Cart cart = getOrCreateCart(user);

        // Перевірка на наявність книги в кошику перед видаленням
        Optional<CartBook> cartBook = cartBookRepository.findByCartIdAndBookId(cart.getId(), book.getId());
        if (cartBook.isPresent()) {
            cartBookRepository.deleteByCartIdAndBookId(cart.getId(), book.getId());
            logger.info("Book '{}' removed from the cart of user with ID: {}", book.getTitle(), user.getId());
        } else {
            logger.error("Book '{}' is not in the cart of user with ID: {}", book.getTitle(), user.getId());
            throw new IllegalArgumentException("This book is not in the cart.");
        }
    }

    /**
     * Повертає вміст кошика користувача у вигляді DTO.
     *
     * @param user Користувач, для якого потрібно отримати вміст кошика.
     * @return Список DTO книг, що знаходяться в кошику.
     * @throws IllegalArgumentException Якщо користувач не авторизований.
     */
    public List<CartBook> getCartContents(User user) {
        if (user == null) {
            logger.error("User is not authorized.");
            throw new IllegalArgumentException("User is not authorized.");
        }
        Cart cart = getOrCreateCart(user);
        List<CartBook> cartContents = cartBookRepository.findByCartId(cart.getId());
        logger.info("Retrieved the cart contents of user with ID: {}. Number of books: {}",
                user.getId(), cartContents.size());
        return cartContents;
    }

    /**
     * Обчислює загальну суму для кошика користувача.
     *
     * @param user Користувач, для якого потрібно обчислити суму кошика.
     * @return Загальна сума кошика.
     * @throws IllegalArgumentException Якщо користувач не авторизований.
     */
    public double getTotalSumForCart(User user) {
        if (user == null) {
            logger.error("User is not authorized.");
            throw new IllegalArgumentException("User is not authorized.");
        }
        Cart cart = getOrCreateCart(user);
        List<CartBook> cartContents = getCartContents(user);
        
        return cartContents.stream()
                .mapToDouble(cartBook -> {
                    int actualPrice = priceCalculationService.calculateActualPrice(cartBook.getBook());
                    return actualPrice * cartBook.getQuantity();
                })
                .sum();
    }
}
