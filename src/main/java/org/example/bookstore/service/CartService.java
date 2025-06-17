package org.example.bookstore.service;

import org.example.bookstore.entity.*;
import org.example.bookstore.repository.CartBookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервісний клас для управління кошиком користувача та гостя
 */
@Service
public class CartService {

    private final CartBookRepository cartBookRepository;
    public CartService( CartBookRepository cartBookRepository) {
        this.cartBookRepository = cartBookRepository;
    }

    /**
     * Додає або змінює кількість книги в кошику авторизованого користувача
     * @param user Користувач, що додає книгу
     * @param book Книга, яку потрібно додати
     * @param quantity Кількість книг
     * @throws IllegalArgumentException Якщо користувач не авторизований, якщо книги немає на складі
     *                                   або кількість перевищує дозволену.
     */
    @Transactional
    public void addOrUpdateBookInCart(User user, Book book, int quantity) {
        if (user == null) {
            throw new IllegalArgumentException("Користувача не знайдено");
        }
        if (book.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("Книга відсутня на складі");
        }
        if (quantity > 10 || quantity > book.getStockQuantity()) {
            throw new IllegalArgumentException("Кількість перевищує ліміт: доступно не більше "
                    + Math.min(10, book.getStockQuantity()) + " книг.");
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
    }

    /**
     * Видаляє книгу з кошика
     * @param user Користувач, що видаляє книгу
     * @param book Книга, яку потрібно видалити
     * @throws IllegalArgumentException Якщо книга відсутня в кошику або користувач не авторизований.
     */
    @Transactional
    public void removeBookFromCart(User user, Book book) {
        if (user == null) {
            throw new IllegalArgumentException("Користувача не знайдено");
        }
        Optional<CartBook> cartBook = cartBookRepository.findByUserIdAndBookId(user.getId(), book.getId());
        if (cartBook.isPresent()) {
            cartBookRepository.deleteByUserIdAndBookId(user.getId(), book.getId());
        } else {
            throw new IllegalArgumentException("Книга відсутня у кошику");
        }
    }

    /**
     * Перевіряє актуальність товарів та видаляє ті, що стали недоступні
     * @param user Користувач
     * @return Список назв книг, які були видалені з кошика
     */
    @Transactional
    public List<String> checkAndCleanCart(User user) {
        List<CartBook> cartBooks = cartBookRepository.findByUserIdOrderByIdAsc(user.getId());
        List<String> removedBooks = new ArrayList<>();

        for (CartBook cartBook : cartBooks) {
            Book book = cartBook.getBook();
            if (book.getStockQuantity() < cartBook.getQuantity() || book.getStockQuantity() <= 0) {
                removedBooks.add(book.getTitle());
                cartBookRepository.deleteByUserIdAndBookId(user.getId(), book.getId());
            }
        }
        return removedBooks;
    }

    /**
     * Повертає вміст кошика користувача.
     * @param user Користувач, для якого потрібно отримати вміст кошика.
     * @return Список книг, що знаходяться в кошику.
     * @throws IllegalArgumentException Якщо користувач не авторизований.
     */
    @Transactional
    public List<CartBook> getCartContents(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Користувача не знайдено");
        }
        return cartBookRepository.findByUserIdOrderByIdAsc(user.getId());
    }

    /**
     * Повертає ідентифікатори книг в кошику користувача.
     * @param user Користувач
     * @return Список ідентифікаторів доданих книг
     * @throws IllegalArgumentException Якщо користувач не авторизований.
     */
    public List<Long> getCartBookIds(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Користувача не знайдено");
        }
        return cartBookRepository.findBookIdsByUserId(user.getId());
    }

    /**
     * Обчислює суму кошика для авторизованого користувача
     * @param user Користувач, для якого треба обчислити суму кошика
     * @return Загальна сума кошика
     * @throws IllegalArgumentException Якщо користувач не авторизований.
     */
    public double getTotalSumForCart(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Користувача не знайдено");
        }
        Integer totalAmount = cartBookRepository.calculateTotalSumByUserId(user.getId());
        return totalAmount != null ? totalAmount : 0;
    }

    /**
     * Повертає загальну кількість книг у кошику авторизованого користувача
     * @param user Користувач
     * @return Загальна кілкьість
     */
    public int getTotalQuantityForUserCart(User user) {
        return cartBookRepository.calculateCartQuantity(user.getId());
    }

    /**
     * Об'єднує кошик гостя з кошиком авторизованого користувача
     * @param guestCart Гостьовий кошик
     * @param user Користувач
     */
   @Transactional
    public void mergeGuestCartWithUserCart(List<CartBook> guestCart, User user) {
        for (CartBook guestItem : guestCart) {
            Book book = guestItem.getBook();
            int quantity = guestItem.getQuantity();

            addOrUpdateBookInCart(user, book, quantity);
        }
    }

    /**
     * Обчислює суму кошика для гостя
     * @param guestCart Гостьовий кошик
     * @return Загальна сума кошика
     * @throws IllegalArgumentException Якщо користувач не авторизований.
     */
    public double getTotalSumForGuestCart(List<CartBook> guestCart) {
        return guestCart.stream()
                .mapToDouble(cartBook -> cartBook.getBook().getActualPrice() * cartBook.getQuantity())
                .sum();
    }

    /**
     * Додає або змінює кількість книги в кошику гостя
     * @param guestCart Гостьовий кошик
     * @param book Книга, яку потрібно додати
     * @param quantity Кількість книг
     * @throws IllegalArgumentException Якщо користувач не авторизований, якщо книги немає на складі
     *                                   або кількість перевищує дозволену.
     */
    public List<CartBook> addOrUpdateBookInGuestCart(List<CartBook> guestCart, Book book, int quantity) {
        if (book.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("Книга відсутня на складі");
        }
        if (quantity > 10 || quantity > book.getStockQuantity()) {
            throw new IllegalArgumentException("Кількість перевищує ліміт: доступно не більше "
                    + Math.min(10, book.getStockQuantity()) + " книг.");
        }
        for (CartBook cartBook : guestCart) {
            if (cartBook.getBook().getId().equals(book.getId())) {
                cartBook.setQuantity(quantity);
                return guestCart;
            }
        }
        CartBook newCartBook = new CartBook();
        newCartBook.setBook(book);
        newCartBook.setQuantity(quantity);
        guestCart.add(newCartBook);
        return guestCart;
    }

    /**
     * Видаляє книгу з кошика
     * @param guestCart Гостьовий кошик
     * @param book Книга, яку потрібно видалити
     */
    public List<CartBook> removeBookFromGuestCart(List<CartBook> guestCart, Book book) {
        guestCart.removeIf(cartBook -> cartBook.getBook().getId().equals(book.getId()));
        return guestCart;
    }

    /**
     * Повертає загальну кількість книг у кошику гостя
     * @param guestCart Гостьовий кошик
     * @return Загальна кілкьість
     */
    public int getTotalQuantityForGuestCart(List<CartBook> guestCart) {
        return guestCart.stream().mapToInt(CartBook::getQuantity).sum();
    }
}
