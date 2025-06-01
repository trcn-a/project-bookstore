package org.example.bookstore.service;

import org.example.bookstore.entity.CartBook;
import org.example.bookstore.entity.User;
import org.example.bookstore.entity.Book;
import org.example.bookstore.repository.CartBookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервісний клас для управління кошиком користувача.
 * Включає бізнес-логіку для додавання/оновлення книг у кошик, видалення книг,
 * отримання вмісту кошика та обчислення загальної суми кошика.
 */
@Service
public class CartService {

    private final CartBookRepository cartBookRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    /**
     * Конструктор, який ініціалізує CartService за допомогою переданих репозиторіїв.
     *
     * @param cartRepository Репозиторій для взаємодії з кошиками користувачів у базі даних.
     * @param cartBookRepository Репозиторій для взаємодії з книжками в кошику.
     */
    public CartService( CartBookRepository cartBookRepository) {
        this.cartBookRepository = cartBookRepository;
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

        if (quantity > 10) {
            logger.error("Quantity exceeds the limit. The maximum number of books in one order is 10.");
            throw new IllegalArgumentException("The maximum number of books in one order is 10.");
        }

        if (quantity > book.getStockQuantity()) {
            logger.error("Not enough books in stock for book: {}. Needed: {}, available in stock: {}",
                    book.getTitle(), quantity, book.getStockQuantity());
            throw new IllegalArgumentException("Not enough books in stock.");
        }

        CartBook cartBook = cartBookRepository.findByUserIdAndBookId(user.getId(), book.getId())
                .orElseGet(() -> {
                    CartBook newCartBook = new CartBook();
                    newCartBook.setUser(user);
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

        Optional<CartBook> cartBook = cartBookRepository.findByUserIdAndBookId(user.getId(), book.getId());
        if (cartBook.isPresent()) {
            cartBookRepository.deleteByUserIdAndBookId(user.getId(), book.getId());
            logger.info("Book '{}' removed from the cart of user with ID: {}", book.getTitle(), user.getId());
        } else {
            logger.error("Book '{}' is not in the cart of user with ID: {}", book.getTitle(), user.getId());
            throw new IllegalArgumentException("This book is not in the cart.");
        }
    }
    @Transactional
    public List<String> checkAndCleanCart(User user) {
        List<CartBook> cartBooks = cartBookRepository.findByUserIdOrderByIdAsc(user.getId());
        List<String> removedBooks = new ArrayList<>();

        for (CartBook cartBook : cartBooks) {
            Book book = cartBook.getBook();
            if (book.getStockQuantity() < cartBook.getQuantity() || book.getStockQuantity() <= 0) {
                removedBooks.add(book.getTitle());
                cartBookRepository.deleteByUserIdAndBookId(user.getId(), book.getId());
                logger.warn("Removed '{}' from user {} cart due to insufficient stock (needed: {}, available: {})",
                        book.getTitle(), user.getId(), cartBook.getQuantity(), book.getStockQuantity());
            }
        }
        return removedBooks;
    }
    /**
     * Повертає вміст кошика користувача.
     *
     * @param user Користувач, для якого потрібно отримати вміст кошика.
     * @return Список книг, що знаходяться в кошику.
     * @throws IllegalArgumentException Якщо користувач не авторизований.
     */


    @Transactional
    public List<CartBook> getCartContents(User user) {

        List<String> removedBooks = checkAndCleanCart(user); // Очищення кошика від відсутніх книг
        if (!removedBooks.isEmpty()) {
            logger.info("Books removed from cart due to insufficient stock: {}", String.join(", ", removedBooks));
        }
        return cartBookRepository.findByUserIdOrderByIdAsc(user.getId());
    }


    public List<Long> getCartBookIds(User user) {
        if (user == null) {
            logger.error("User is not authorized.");
            throw new IllegalArgumentException("User is not authorized.");
        }
        List<Long> cartContents = cartBookRepository.findBookIdsByUserId(user.getId());
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
        Integer totalAmount = cartBookRepository.calculateTotalSumByUserId(user.getId());
        double totalSum = totalAmount != null ? totalAmount : 0;
        logger.info("Calculated the total sum of the cart for user with ID: {}. Total sum: {}", user.getId(), totalSum);
        return totalSum;
    }

    public double getTotalSumForGuestCart(List<CartBook> guestCart) {
        return guestCart.stream()
                .mapToDouble(cartBook -> cartBook.getBook().getActualPrice() * cartBook.getQuantity())
                .sum();
    }

    public List<CartBook> addOrUpdateBookInGuestCart(List<CartBook> guestCart, Book book, int quantity) {
        // Шукаємо, чи є вже ця книга в кошику
        for (CartBook cartBook : guestCart) {
            if (cartBook.getBook().getId().equals(book.getId())) {

                cartBook.setQuantity(quantity);
                return guestCart;
            }
        }

        // Якщо книги немає в кошику, додаємо нову книгу
        CartBook newCartBook = new CartBook();
        newCartBook.setBook(book);
        newCartBook.setQuantity(quantity);
        guestCart.add(newCartBook);
        return guestCart;
    }

    public List<CartBook> removeBookFromGuestCart(List<CartBook> guestCart, Book book) {
        guestCart.removeIf(cartBook -> cartBook.getBook().getId().equals(book.getId()));
        return guestCart;
    }

    public void mergeGuestCartWithUserCart(List<CartBook> guestCart, User user) {
        for (CartBook guestItem : guestCart) {
            Book book = guestItem.getBook();
            int quantity = guestItem.getQuantity();

            addOrUpdateBookInCart(user, book, quantity);
        }
    }
}
