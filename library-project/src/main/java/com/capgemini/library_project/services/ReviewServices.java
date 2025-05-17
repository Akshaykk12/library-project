package com.capgemini.library_project.services;

import java.util.List;

import com.capgemini.library_project.entities.Review;

public interface ReviewServices {
	List<Review> getAllReviews();

	Review getReviewById(Long id);

	Review createReview(Review review);

	Review updateReview(Long id, Review review);

	boolean deleteReview(Long id);

	Review addReviewToBook(Long bookId, Review review);

	void assignReviewToBook(Long bookId, Long reviewId);

	Review addReviewToUser(Long userId, Review review);

	void assignReviewToUser(Long userId, Long reviewId);

	List<Review> getReviewsByBookId(Long bookId);

	List<Review> getReviewsByUserId(Long userId);

	Double getAverageRatingByBookId(Long bookId);

	List<Review> getReviewsWithMinRating(int minRating);

	Long countReviewsByBookId(Long bookId);

}
