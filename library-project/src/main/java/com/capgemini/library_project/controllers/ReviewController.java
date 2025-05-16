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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
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
	public ResponseEntity<Review> deleteCourse(@PathVariable Long id) {
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
