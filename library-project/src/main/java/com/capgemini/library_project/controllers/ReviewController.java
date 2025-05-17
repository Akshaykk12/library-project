package com.capgemini.library_project.controllers;

import java.util.List;

import com.capgemini.library_project.entities.Review;
import com.capgemini.library_project.services.ReviewServices;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class ReviewController {

	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
	private final ReviewServices reviewServices;

	@Autowired
	public ReviewController(ReviewServices reviewServices) {
		this.reviewServices = reviewServices;
	}

	@GetMapping
	public ResponseEntity<List<Review>> getAllReviews() {
		logger.info("GET request received: fetch all reviews");
		return ResponseEntity.ok(reviewServices.getAllReviews());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
		logger.info("GET request received: fetch review by ID {}", id);
		return ResponseEntity.ok(reviewServices.getReviewById(id));
	}

	@GetMapping("/book/{bookId}")
	public ResponseEntity<List<Review>> getReviewsByBook(@PathVariable Long bookId) {
		logger.info("GET request received: fetch all reviews for book ID {}", bookId);
		List<Review> reviews = reviewServices.getReviewsByBookId(bookId);
		return ResponseEntity.ok(reviews);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
		logger.info("GET request received: fetch all reviews for user ID {}", userId);
		List<Review> reviews = reviewServices.getReviewsByUserId(userId);
		return ResponseEntity.ok(reviews);
	}

	@GetMapping("/book/{bookId}/average-rating")
	public ResponseEntity<Double> getAverageRating(@PathVariable Long bookId) {
		logger.info("GET request: average rating for book ID {}", bookId);
		return ResponseEntity.ok(reviewServices.getAverageRatingByBookId(bookId));
	}

	@GetMapping("/min-rating/{minRating}")
	public ResponseEntity<List<Review>> getReviewsWithMinRating(@PathVariable int minRating) {
		logger.info("GET request: reviews with minimum rating {}", minRating);
		return ResponseEntity.ok(reviewServices.getReviewsWithMinRating(minRating));
	}

	@GetMapping("/book/{bookId}/count")
	public ResponseEntity<Long> countReviews(@PathVariable Long bookId) {
		logger.info("GET request: count reviews for book ID {}", bookId);
		return ResponseEntity.ok(reviewServices.countReviewsByBookId(bookId));
	}

	@PostMapping
	public ResponseEntity<Review> createReview(@Valid @RequestBody Review review, BindingResult bindingResult) {
		logger.info("POST request received: create new review");
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		Review saved = reviewServices.createReview(review);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Review> updateReview(@PathVariable Long id, @Valid @RequestBody Review newReview,
			BindingResult bindingResult) {
		logger.info("PUT request received: update review with ID {}", id);
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		Review updated = reviewServices.updateReview(id, newReview);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Review> deleteReview(@PathVariable Long id) {
		logger.info("DELETE request received: delete review with ID {}", id);
		reviewServices.deleteReview(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{bookId}/assignBook/{reviewId}")
	public ResponseEntity<Void> assignReviewToBook(@PathVariable Long bookId, @PathVariable Long reviewId) {
		logger.info("POST request received: assign review ID {} to book ID {}", reviewId, bookId);
		reviewServices.assignReviewToBook(bookId, reviewId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{bookId}/enrollBook")
	public ResponseEntity<Review> assignReviewToBook(@PathVariable Long bookId, @Valid @RequestBody Review review,
			BindingResult bindingResult) {
		logger.info("POST request received: add review to book ID {}", bookId);
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(reviewServices.addReviewToBook(bookId, review));
	}

	@PostMapping("/{userId}/assignUser/{reviewId}")
	public ResponseEntity<Void> assignReviewToUser(@PathVariable Long userId, @PathVariable Long reviewId) {
		logger.info("POST request received: assign review ID {} to user ID {}", reviewId, userId);
		reviewServices.assignReviewToUser(userId, reviewId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{userId}/enrollUser")
	public ResponseEntity<Review> assignReviewToUser(@PathVariable Long userId, @Valid @RequestBody Review review,
			BindingResult bindingResult) {
		logger.info("POST request received: add review to user ID {}", userId);
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(reviewServices.addReviewToUser(userId, review));
	}
}
