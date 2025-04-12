package org.example.bookstore.Services;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.Favorite;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Repositories.BookRepository;
import org.example.bookstore.Repositories.FavoriteRepository;
import org.example.bookstore.Repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
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
                .orElseThrow(() -> new RuntimeException("Користувач не знайдений"));
        return favoriteRepository.findByUser(user)
                .stream()
                .map(Favorite::getBook)
                .collect(Collectors.toList());
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
                .orElseThrow(() -> new RuntimeException("Користувач не знайдений"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не знайдена"));

        if (favoriteRepository.findByUserAndBook(user, book).isEmpty()) {
            Favorite favorite = new Favorite(user, book);
            favoriteRepository.save(favorite);
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
                .orElseThrow(() -> new RuntimeException("Користувач не знайдений"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Книга не знайдена"));

        favoriteRepository.deleteByUserAndBook(user, book);
    }
}
