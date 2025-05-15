package com.capgemini.library_project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.Review;
import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.ReviewRepository;
import com.capgemini.library_project.repositories.UserRepository;

@Service
public class ReviewServicesImpl implements ReviewServices{
	
	private final ReviewRepository reviewRepository;
	private final BookRepository bookRepository;
	private final UserRepository userRepository;
	
	@Autowired
	public ReviewServicesImpl(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
		this.reviewRepository = reviewRepository;
		this.bookRepository = bookRepository;
		this.userRepository = userRepository;
	}
	
	@Override
	public List<Review> getAllReviews() {
		return reviewRepository.findAll();
	}
	
	@Override
	public Review getReviewById(Long id) {
		return reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review with Id " + id + "not found"));
	}
	
	@Override
	public Review createReview(Review review) {
		return reviewRepository.save(review);
	}
	
	@Override
	public Review updateReview(Long id, Review updated) {
		Review exist = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review with Id " + id + "not found"));
		exist.setFeedback(updated.getFeedback());
		exist.setRating(updated.getRating());
		return reviewRepository.save(exist);
	}
	
	@Override
	public boolean deleteReview(Long id) {
		if(reviewRepository.existsById(id)) {
			reviewRepository.deleteById(id);
			return true;
		}
		return false;
	}
	
	@Override
	public Review addReviewToBook(Long bookId, Review review) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new RuntimeException("Book Not Found"));
		
		review.setBook(book);
		book.getReviews().add(review);
		
		return reviewRepository.save(review);
	}
	
	@Override
	public void assignReviewToBook(Long bookId, Long reviewId) {
		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new RuntimeException("Book Not Found"));
		
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new RuntimeException("Review Not Found"));
		
		book.getReviews().add(review);
		review.setBook(book);
		bookRepository.save(book);		
	}
	
	@Override
	public Review addReviewToUser(Long userId, Review review) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));
		
		review.setUser(user);
		user.getReviews().add(review);
		
		return reviewRepository.save(review);
	}
	
	@Override
	public void assignReviewToUser(Long userId, Long reviewId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));
		
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new RuntimeException("Review Not Found"));
		user.getReviews().add(review);
		review.setUser(user);
		userRepository.save(user);
		
	}

}
