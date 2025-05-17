package com.capgemini.library_project.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.Review;
import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.exceptions.BookNotFoundException;
import com.capgemini.library_project.exceptions.ReviewNotFoundException;
import com.capgemini.library_project.exceptions.UserNotFoundException;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.ReviewRepository;
import com.capgemini.library_project.repositories.UserRepository;

@Service
public class ReviewServicesImpl implements ReviewServices {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServicesImpl.class);

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewServicesImpl(ReviewRepository reviewRepository,
                              BookRepository bookRepository,
                              UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Review> getAllReviews() {
        logger.info("Fetching all reviews");
        return reviewRepository.findAll();
    }

    @Override
    public Review getReviewById(Long id) {
        logger.info("Fetching review by ID: {}", id);
        return reviewRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Review with ID {} not found", id);
                    return new ReviewNotFoundException("Review with Id " + id + " not found");
                });
    }

    @Override
    public Review createReview(Review review) {
        logger.info("Creating new review");
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long id, Review updated) {
        logger.info("Updating review with ID: {}", id);
        Review exist = reviewRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Review with ID {} not found for update", id);
                    return new ReviewNotFoundException("Review with Id " + id + " not found");
                });
        exist.setFeedback(updated.getFeedback());
        exist.setRating(updated.getRating());
        return reviewRepository.save(exist);
    }

    @Override
    public boolean deleteReview(Long id) {
        logger.info("Deleting review with ID: {}", id);
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            logger.info("Review with ID {} successfully deleted", id);
            return true;
        } else {
            logger.warn("Review with ID {} not found for deletion", id);
            return false;
        }
    }

    @Override
    public Review addReviewToBook(Long bookId, Review review) {
        logger.info("Adding review to book ID: {}", bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Book with ID {} not found", bookId);
                    return new BookNotFoundException("Book with ID " + bookId + " not found.");
                });

        review.setBook(book);
        book.getReviews().add(review);
        return reviewRepository.save(review);
    }

    @Override
    public void assignReviewToBook(Long bookId, Long reviewId) {
        logger.info("Assigning review ID {} to book ID {}", reviewId, bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Book with ID {} not found", bookId);
                    return new BookNotFoundException("Book with ID " + bookId + " not found.");
                });

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("Review with ID {} not found", reviewId);
                    return new ReviewNotFoundException("Review with Id " + reviewId + " not found");
                });

        book.getReviews().add(review);
        review.setBook(book);
        bookRepository.save(book);
    }

    @Override
    public Review addReviewToUser(Long userId, Review review) {
        logger.info("Adding review to user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User with id : " + userId + " not found.");
                });

        review.setUser(user);
        user.getReviews().add(review);
        return reviewRepository.save(review);
    }

    @Override
    public void assignReviewToUser(Long userId, Long reviewId) {
        logger.info("Assigning review ID {} to user ID {}", reviewId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User with id : " + userId + " not found.");
                });

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("Review with ID {} not found", reviewId);
                    return new ReviewNotFoundException("Review with Id " + reviewId + " not found");
                });

        user.getReviews().add(review);
        review.setUser(user);
        userRepository.save(user);
    }
}
