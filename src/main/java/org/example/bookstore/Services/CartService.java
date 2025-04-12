package org.example.bookstore.Services;

import org.example.bookstore.Entities.Cart;
import org.example.bookstore.Entities.CartBook;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Entities.Book;
import org.example.bookstore.Repositories.CartBookRepository;
import org.example.bookstore.Repositories.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
/**
 * Сервісний клас для управління кошиком користувача.
 * Включає бізнес-логіку для додавання/оновлення книг у кошик, видалення книг,
 * отримання вмісту кошика та обчислення загальної суми кошика.
 */
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartBookRepository cartBookRepository;

    /**
     * Конструктор, який ініціалізує CartService за допомогою переданих репозиторіїв.
     *
     * @param cartRepository Репозиторій для взаємодії з кошиками користувачів у базі даних.
     * @param cartBookRepository Репозиторій для взаємодії з книжками в кошику.
     */
    public CartService(CartRepository cartRepository, CartBookRepository cartBookRepository) {
        this.cartRepository = cartRepository;
        this.cartBookRepository = cartBookRepository;
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
            throw new IllegalArgumentException("Користувач не авторизований.");
        }
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
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
            throw new IllegalArgumentException("Користувач не авторизований.");
        }
        if (book.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("Книга наразі відсутня на складі.");
        }

        // Обмеження кількості книг в кошику — не більше 10 одиниць
        if (quantity > 10) {
            throw new IllegalArgumentException("Максимальна кількість книг в одному замовленні - 10.");
        }

        // Перевірка на те, чи кількість, яку додає користувач, не перевищує наявну кількість на складі
        if (quantity > book.getStockQuantity()) {
            throw new IllegalArgumentException("Недостатньо книг на складі.");
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
            throw new IllegalArgumentException("Користувач не авторизований.");
        }
        Cart cart = getOrCreateCart(user);

        // Перевірка на наявність книги в кошику перед видаленням
        Optional<CartBook> cartBook = cartBookRepository.findByCartIdAndBookId(cart.getId(), book.getId());
        if (cartBook.isPresent()) {
            cartBookRepository.deleteByCartIdAndBookId(cart.getId(), book.getId());
        } else {
            throw new IllegalArgumentException("Ця книга відсутня в кошику.");
        }
    }

    /**
     * Повертає вміст кошика користувача.
     *
     * @param user Користувач, для якого потрібно отримати вміст кошика.
     * @return Список книг, що знаходяться в кошику.
     * @throws IllegalArgumentException Якщо користувач не авторизований.
     */
    public List<CartBook> getCartContents(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Користувач не авторизований.");
        }
        Cart cart = getOrCreateCart(user);
        return cartBookRepository.findByCartId(cart.getId());
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
            throw new IllegalArgumentException("Користувач не авторизований.");
        }
        Cart cart = getOrCreateCart(user);
        Integer totalAmount = cartBookRepository.calculateTotalSumByCartId(cart.getId());
        return totalAmount != null ? totalAmount : 0;
    }
}
