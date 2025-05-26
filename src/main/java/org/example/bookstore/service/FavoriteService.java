package org.example.bookstore.service;

import org.example.bookstore.entity.Book;
import org.example.bookstore.entity.Favorite;
import org.example.bookstore.entity.User;
import org.example.bookstore.repository.BookRepository;
import org.example.bookstore.repository.FavoriteRepository;
import org.example.bookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Сервісний клас для управління списком обраних книг користувачів.
 * Включає бізнес-логіку для додавання/видалення книг до/з обраних,
 * а також для отримання списку обраних книг користувача.
 */
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private static final Logger logger = LoggerFactory.getLogger(FavoriteService.class);

    /**
     * Конструктор, який ініціалізує FavoriteService необхідними репозиторіями
     * для роботи з обраними книгами користувачів.
     *
     * @param favoriteRepository Репозиторій для збереження та отримання обраних книг.
     * @param userRepository Репозиторій для взаємодії з даними користувачів.
     * @param bookRepository Репозиторій для доступу до інформації про книги.
     */
    public FavoriteService(FavoriteRepository favoriteRepository,
                           UserRepository userRepository,
                           BookRepository bookRepository) {

        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Отримує список обраних книг користувача.
     *
     * @param userId Ідентифікатор користувача.
     * @return Список обраних книг користувача.
     * @throws RuntimeException Якщо користувач не знайдений.
     */
    public List<Book> getFavoriteBooks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new RuntimeException("User with ID " + userId + " not found");
                });

        List<Book> favoriteBooks = favoriteRepository.findBooksByUser(user);

        logger.info("Fetched {} favorite books for user with ID: {}", favoriteBooks.size(), userId);
        return favoriteBooks;
    }

    public List<Long> getFavoriteBookIds(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new RuntimeException("User with ID " + userId + " not found");
                });

        List<Long> favoriteBookIds = favoriteRepository.findBookIdsByUser(user);

        logger.info("Fetched {} favorite books for user with ID: {}", favoriteBookIds.size(), userId);
        return favoriteBookIds;
    }


    /**
     * Додає книгу до списку обраних користувача.
     *
     * @param userId Ідентифікатор користувача.
     * @param bookId Ідентифікатор книги.
     * @throws RuntimeException Якщо користувач або книга не знайдені.
     */
    @Transactional
    public void addToFavorites(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new RuntimeException("User not found");
                });
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Book with ID {} not found", bookId);
                    return new RuntimeException("Book not found");
                });

        if (favoriteRepository.findByUserAndBook(user, book).isEmpty()) {
            Favorite favorite = new Favorite(user, book);
            favoriteRepository.save(favorite);
            logger.info("Book with ID {} added to user {}'s favorites", bookId, userId);
        } else {
            logger.warn("Book with ID {} already exists in user {}'s favorites", bookId, userId);
        }
    }

    /**
     * Видаляє книгу з обраних користувача.
     *
     * @param userId Ідентифікатор користувача.
     * @param bookId Ідентифікатор книги.
     * @throws RuntimeException Якщо користувач або книга не знайдені.
     */
    @Transactional
    public void removeFromFavorites(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new RuntimeException("User not found");
                });
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Book with ID {} not found", bookId);
                    return new RuntimeException("Book not found");
                });

        favoriteRepository.deleteByUserAndBook(user, book);
        logger.info("Book with ID {} removed from user {}'s favorites", bookId, userId);
    }
}
