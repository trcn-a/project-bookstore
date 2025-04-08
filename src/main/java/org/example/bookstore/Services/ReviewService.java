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

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
        private final BookRepository bookRepository;
        private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

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


    public List<Review> getReviewsByBook(Long bookId) {

        return reviewRepository.findByBookId(bookId);
    }

    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId);
    }


    public void deleteReview(Long bookId, Long userId) {
        Review review = reviewRepository.findByBookIdAndUserId(bookId, userId);
        if (review != null) {
            reviewRepository.delete(review);
        }
    }

    public double getAverageRating(Long bookId) {
        return reviewRepository.findAverageRatingByBookId(bookId);
    }

    public Review getReviewByBookIdAndUserId(Long bookId, Long userId) {
        return reviewRepository.findByBookIdAndUserId(bookId, userId);
    }
}


