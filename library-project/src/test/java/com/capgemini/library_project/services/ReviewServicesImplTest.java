// ReviewServicesImplTest.java
package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Review;
import com.capgemini.library_project.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServicesImplTest {

	@Mock
	private ReviewRepository reviewRepository;

	@InjectMocks
	private ReviewServicesImpl reviewServices;

	private Review review;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		review = new Review();
		review.setReviewId(1L);
		review.setFeedback("Good");
		review.setRating(4);
	}

	@Test
	void testGetAllReviews() {
		when(reviewRepository.findAll()).thenReturn(Collections.singletonList(review));
		List<Review> result = reviewServices.getAllReviews();
		assertEquals(1, result.size());
	}

	@Test
	void testGetReviewById() {
		when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
		Review result = reviewServices.getReviewById(1L);
		assertEquals("Good", result.getFeedback());
	}

	@Test
	void testCreateReview() {
		when(reviewRepository.save(review)).thenReturn(review);
		Review result = reviewServices.createReview(review);
		assertEquals(4, result.getRating());
	}

	@Test
	void testUpdateReview() {
		Review updated = new Review();
		updated.setFeedback("Excellent");
		updated.setRating(5);

		when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
		when(reviewRepository.save(any(Review.class))).thenReturn(review);

		Review result = reviewServices.updateReview(1L, updated);
		assertEquals("Excellent", result.getFeedback());
	}

	@Test
	void testDeleteReview_Success() {
		when(reviewRepository.existsById(1L)).thenReturn(true);
		boolean deleted = reviewServices.deleteReview(1L);
		assertTrue(deleted);
		verify(reviewRepository).deleteById(1L);
	}

	@Test
	void testDeleteReview_NotFound() {
		when(reviewRepository.existsById(1L)).thenReturn(false);
		boolean deleted = reviewServices.deleteReview(1L);
		assertFalse(deleted);
	}
}