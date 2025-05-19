package com.capgemini.library_project.controllers;

import com.capgemini.library_project.entities.Review;
import com.capgemini.library_project.services.ReviewServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewControllerTest {

	@Mock
	private ReviewServices reviewServices;

	@InjectMocks
	private ReviewController reviewController;

	private Review sampleReview;

	@Mock
	private BindingResult bindingResult;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		sampleReview = new Review();
		sampleReview.setReviewId(1L);
		sampleReview.setFeedback("Excellent");
		sampleReview.setRating(5);
	}

	@Test
	void testGetAllReviews() {
		when(reviewServices.getAllReviews()).thenReturn(Arrays.asList(sampleReview));

		ResponseEntity<List<Review>> response = reviewController.getAllReviews();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
		assertEquals("Excellent", response.getBody().get(0).getFeedback());
	}

	@Test
	void testGetReviewById() {
		when(reviewServices.getReviewById(1L)).thenReturn(sampleReview);

		ResponseEntity<Review> response = reviewController.getReviewById(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Excellent", response.getBody().getFeedback());
	}

	@Test
	void testCreateReview() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(reviewServices.createReview(sampleReview)).thenReturn(sampleReview);

		ResponseEntity<Review> response = reviewController.createReview(sampleReview, bindingResult);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Excellent", response.getBody().getFeedback());
	}

	@Test
	void testUpdateReview_Found() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(reviewServices.updateReview(eq(1L), any(Review.class))).thenReturn(sampleReview);

		ResponseEntity<Review> response = reviewController.updateReview(1L, sampleReview, bindingResult);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Excellent", response.getBody().getFeedback());
	}

	@Test
	void testUpdateReview_NotFound() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(reviewServices.updateReview(eq(1L), any(Review.class))).thenReturn(null);

		ResponseEntity<Review> response = reviewController.updateReview(1L, sampleReview, bindingResult);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void testDeleteReview() {
		when(reviewServices.deleteReview(1L)).thenReturn(true);

		ResponseEntity<Review> response = reviewController.deleteReview(1L);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void testGetReviewsByBook() {
		when(reviewServices.getReviewsByBookId(1L)).thenReturn(Arrays.asList(sampleReview));

		ResponseEntity<List<Review>> response = reviewController.getReviewsByBook(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
		assertEquals("Excellent", response.getBody().get(0).getFeedback());
	}

	@Test
	void testGetReviewsByUser() {
		when(reviewServices.getReviewsByUserId(1L)).thenReturn(Arrays.asList(sampleReview));

		ResponseEntity<List<Review>> response = reviewController.getReviewsByUser(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
		assertEquals("Excellent", response.getBody().get(0).getFeedback());
	}

	@Test
	void testGetAverageRating() {
		when(reviewServices.getAverageRatingByBookId(1L)).thenReturn(4.5);

		ResponseEntity<Double> response = reviewController.getAverageRating(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(4.5, response.getBody());
	}

	@Test
	void testGetReviewsWithMinRating() {
		when(reviewServices.getReviewsWithMinRating(4)).thenReturn(Arrays.asList(sampleReview));

		ResponseEntity<List<Review>> response = reviewController.getReviewsWithMinRating(4);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
		assertEquals(5, response.getBody().get(0).getRating());
	}

	@Test
	void testCountReviews() {
		when(reviewServices.countReviewsByBookId(1L)).thenReturn(5L);

		ResponseEntity<Long> response = reviewController.countReviews(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(5L, response.getBody());
	}

	@Test
	void testAssignReviewToBook_Success() {
		doNothing().when(reviewServices).assignReviewToBook(1L, 1L);

		ResponseEntity<Void> response = reviewController.assignReviewToBook(1L, 1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(reviewServices).assignReviewToBook(1L, 1L);
	}

	@Test
	void testAssignReviewToUser_Success() {
		doNothing().when(reviewServices).assignReviewToUser(1L, 1L);

		ResponseEntity<Void> response = reviewController.assignReviewToUser(1L, 1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(reviewServices).assignReviewToUser(1L, 1L);
	}

	@Test
	void testAssignReviewToBookWithReview_Success() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(reviewServices.addReviewToBook(1L, sampleReview)).thenReturn(sampleReview);

		ResponseEntity<Review> response = reviewController.assignReviewToBook(1L, sampleReview, bindingResult);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Excellent", response.getBody().getFeedback());
	}

	@Test
	void testAssignReviewToBookWithReview_ValidationError() {
		when(bindingResult.hasErrors()).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> {
			reviewController.assignReviewToBook(1L, sampleReview, bindingResult);
		});
	}

	@Test
	void testAssignReviewToUserWithReview_Success() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(reviewServices.addReviewToUser(1L, sampleReview)).thenReturn(sampleReview);

		ResponseEntity<Review> response = reviewController.assignReviewToUser(1L, sampleReview, bindingResult);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Excellent", response.getBody().getFeedback());
	}

	@Test
	void testAssignReviewToUserWithReview_ValidationError() {
		when(bindingResult.hasErrors()).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> {
			reviewController.assignReviewToUser(1L, sampleReview, bindingResult);
		});
	}
}
