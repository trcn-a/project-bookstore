package org.example.bookstore.service;

import org.example.bookstore.entity.Book;
import org.example.bookstore.entity.Review;
import org.example.bookstore.entity.User;
import org.example.bookstore.repository.BookRepository;
import org.example.bookstore.repository.ReviewRepository;
import org.example.bookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
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
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

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
                .orElseThrow(() -> {
                    logger.error("Book with ID {} not found", bookId);
                    return new IllegalArgumentException("Book not found");
                });
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new IllegalArgumentException("User not found");
                });
        if (rating < 1 || rating > 5) {
            logger.error("Invalid rating {}. Rating must be between 1 and 5.", rating);
            throw new IllegalArgumentException("Оцінка повинна бути в межах від 1 до 5.");
        }
        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(rating);
        review.setReviewText(comment);
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);
        logger.info("User with ID {} left a review for book with ID {} with rating {}", userId, bookId, rating);
    }

    /**
     * Отримує список всіх відгуків для конкретної книги.
     *
     * @param bookId Ідентифікатор книги.
     * @return Список відгуків на книгу.
     */
    public List<Review> getReviewsByBook(Long bookId) {
        logger.info("Fetching reviews for book with ID {}", bookId);
        return reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId);
    }

    /**
     * Отримує список всіх відгуків, написаних конкретним користувачем.
     *
     * @param userId Ідентифікатор користувача.
     * @return Список відгуків користувача.
     */
    public List<Review> getUserReviews(Long userId) {
        logger.info("Fetching reviews by user with ID {}", userId);
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
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
            logger.info("Review by user with ID {} for book with ID {} deleted", userId, bookId);
        } else {
            logger.error("Review by user with ID {} for book with ID {} not found", userId, bookId);
        }
    }

    /**
     * Отримує середній рейтинг для книги.
     *
     * @param bookId Ідентифікатор книги.
     * @return Середній рейтинг книги.
     */
    public double getAverageRating(Long bookId) {
        double average = reviewRepository.findAverageRatingByBookId(bookId);
        logger.info("The average rating for book with ID {} is {}", bookId, average);
        return average;
    }

    /**
     * Отримує відгук користувача на конкретну книгу.
     *
     * @param bookId Ідентифікатор книги.
     * @param userId Ідентифікатор користувача.
     * @return Відгук користувача на книгу.
     */
    public Review getReviewByBookIdAndUserId(Long bookId, Long userId) {
        logger.info("Fetching review by user with ID {} for book with ID {}", userId, bookId);
        return reviewRepository.findByBookIdAndUserId(bookId, userId);
    }
    public List<Review> findAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }

    public List<Review> findReviewsByBookTitle(String title) {
        return reviewRepository.findByBookTitleContainingIgnoreCase(title);
    }

}
