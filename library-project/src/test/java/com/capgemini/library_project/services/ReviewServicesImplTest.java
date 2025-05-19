package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.Review;
import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.exceptions.BookNotFoundException;
import com.capgemini.library_project.exceptions.ReviewNotFoundException;
import com.capgemini.library_project.exceptions.UserNotFoundException;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.ReviewRepository;
import com.capgemini.library_project.repositories.UserRepository;

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

	@Mock
	private UserRepository userRepository;

	@Mock
	private BookRepository bookRepository;

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

	@Test
	void testCountReviewsByBookId() {
		when(reviewRepository.countReviewsByBookId(1L)).thenReturn(5L);
		Long count = reviewServices.countReviewsByBookId(1L);
		assertEquals(5L, count);
	}

	@Test
	void testGetReviewsWithMinRating() {
		Review highRatingReview = new Review();
		highRatingReview.setRating(5);
		when(reviewRepository.findReviewsWithMinRating(4)).thenReturn(Collections.singletonList(highRatingReview));

		List<Review> result = reviewServices.getReviewsWithMinRating(4);
		assertEquals(1, result.size());
		assertEquals(5, result.get(0).getRating());
	}

	@Test
	void testGetAverageRatingByBookId() {
		when(reviewRepository.findAverageRatingByBookId(1L)).thenReturn(4.5);
		Double average = reviewServices.getAverageRatingByBookId(1L);
		assertEquals(4.5, average);
	}

	@Test
	void testGetReviewsByUserId() {
		Review userReview = new Review();
		userReview.setReviewId(2L);
		when(reviewRepository.findByUserId(1L)).thenReturn(Collections.singletonList(userReview));

		List<Review> result = reviewServices.getReviewsByUserId(1L);
		assertEquals(1, result.size());
		assertEquals(2L, result.get(0).getReviewId());
	}

	@Test
	void testGetReviewsByBookId() {
		Review bookReview = new Review();
		bookReview.setReviewId(3L);
		when(reviewRepository.findByBookId(1L)).thenReturn(Collections.singletonList(bookReview));

		List<Review> result = reviewServices.getReviewsByBookId(1L);
		assertEquals(1, result.size());
		assertEquals(3L, result.get(0).getReviewId());
	}

	@Test
	void testAssignReviewToUser_Success() {
		User user = new User();
		user.setUserId(1L);
		user.setReviews(new ArrayList<>()); // 👈 Fix

		Review assignedReview = new Review();
		assignedReview.setReviewId(2L);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(reviewRepository.findById(2L)).thenReturn(Optional.of(assignedReview));

		reviewServices.assignReviewToUser(1L, 2L);

		verify(userRepository).save(user);
		assertTrue(user.getReviews().contains(assignedReview));
		assertEquals(user, assignedReview.getUser());
	}

	@Test
	void testAssignReviewToUser_UserNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> reviewServices.assignReviewToUser(1L, 2L));
	}

	@Test
	void testAssignReviewToUser_ReviewNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
		when(reviewRepository.findById(2L)).thenReturn(Optional.empty());

		assertThrows(ReviewNotFoundException.class, () -> reviewServices.assignReviewToUser(1L, 2L));
	}

	@Test
	void testAddReviewToUser_UserNotFound() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		Review reviewToAdd = new Review();
		assertThrows(UserNotFoundException.class, () -> reviewServices.addReviewToUser(1L, reviewToAdd));
	}

	@Test
	void testAssignReviewToBook_Success() {
		Book book = new Book();
		book.setBookId(1L);
		book.setReviews(new ArrayList<>()); // 👈 Fix

		Review assignedReview = new Review();
		assignedReview.setReviewId(2L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(reviewRepository.findById(2L)).thenReturn(Optional.of(assignedReview));

		reviewServices.assignReviewToBook(1L, 2L);

		verify(bookRepository).save(book);
		assertTrue(book.getReviews().contains(assignedReview));
		assertEquals(book, assignedReview.getBook());
	}

	@Test
	void testAssignReviewToBook_BookNotFound() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(BookNotFoundException.class, () -> reviewServices.assignReviewToBook(1L, 2L));
	}

	@Test
	void testAssignReviewToBook_ReviewNotFound() {
		when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
		when(reviewRepository.findById(2L)).thenReturn(Optional.empty());

		assertThrows(ReviewNotFoundException.class, () -> reviewServices.assignReviewToBook(1L, 2L));
	}

	@Test
	void testAddReviewToBook_BookNotFound() {
		when(bookRepository.findById(1L)).thenReturn(Optional.empty());
		Review reviewToAdd = new Review();
		assertThrows(BookNotFoundException.class, () -> reviewServices.addReviewToBook(1L, reviewToAdd));
	}

	@Test
	void testUpdateReview_NotFound() {
		when(reviewRepository.findById(1L)).thenReturn(Optional.empty());
		Review updatedReview = new Review();
		assertThrows(ReviewNotFoundException.class, () -> reviewServices.updateReview(1L, updatedReview));
	}

	@Test
	void testGetReviewById_NotFound() {
		when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> reviewServices.getReviewById(1L));
	}
}
