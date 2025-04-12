package org.example.bookstore.Services;

import org.example.bookstore.Entities.Book;
import org.example.bookstore.Entities.Review;
import org.example.bookstore.Entities.User;
import org.example.bookstore.Repositories.BookRepository;
import org.example.bookstore.Repositories.ReviewRepository;
import org.example.bookstore.Repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервісний клас для управління відгуками користувачів на книги.
 * Включає методи для додавання відгуків, отримання відгуків по книгах і користувачах,
 * а також для видалення відгуків та отримання середнього рейтингу книги.
 */
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * Конструктор, який ініціалізує ReviewService з переданими репозиторіями
     * для роботи з відгуками, книгами та користувачами.
     *
     * @param reviewRepository Репозиторій для взаємодії з відгуками користувачів.
     * @param bookRepository Репозиторій для доступу до інформації про книги.
     * @param userRepository Репозиторій для обробки даних користувачів.
     */
    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository,
                         UserRepository userRepository) {

        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Додає відгук користувача до книги.
     *
     * @param bookId   Ідентифікатор книги, до якої додається відгук.
     * @param userId   Ідентифікатор користувача, який залишає відгук.
     * @param rating   Оцінка книги від 1 до 5.
     * @param comment  Текст відгуку.
     * @throws IllegalArgumentException Якщо книга або користувач не знайдені.
     */
    @Transactional
    public void addReview(Long bookId, Long userId, int rating, String comment) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(rating);
        review.setReviewText(comment);

        reviewRepository.save(review);
    }

    /**
     * Отримує список всіх відгуків для конкретної книги.
     *
     * @param bookId Ідентифікатор книги.
     * @return Список відгуків на книгу.
     */
    public List<Review> getReviewsByBook(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    /**
     * Отримує список всіх відгуків, написаних конкретним користувачем.
     *
     * @param userId Ідентифікатор користувача.
     * @return Список відгуків користувача.
     */
    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    /**
     * Видаляє відгук користувача на книгу.
     *
     * @param bookId Ідентифікатор книги.
     * @param userId Ідентифікатор користувача.
     */
    public void deleteReview(Long bookId, Long userId) {
        Review review = reviewRepository.findByBookIdAndUserId(bookId, userId);
        if (review != null) {
            reviewRepository.delete(review);
        }
    }

    /**
     * Отримує середній рейтинг для книги.
     *
     * @param bookId Ідентифікатор книги.
     * @return Середній рейтинг книги.
     */
    public double getAverageRating(Long bookId) {
        return reviewRepository.findAverageRatingByBookId(bookId);
    }

    /**
     * Отримує відгук користувача на конкретну книгу.
     *
     * @param bookId Ідентифікатор книги.
     * @param userId Ідентифікатор користувача.
     * @return Відгук користувача на книгу.
     */
    public Review getReviewByBookIdAndUserId(Long bookId, Long userId) {
        return reviewRepository.findByBookIdAndUserId(bookId, userId);
    }
}
