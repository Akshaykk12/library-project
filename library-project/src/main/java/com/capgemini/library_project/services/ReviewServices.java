package com.capgemini.library_project.services;

import java.util.List;

import com.capgemini.library_project.entities.Review;

public interface ReviewServices {
	List<Review> getAllReviews();
	
	Review getReviewById(Long id);
	
	Review createReview(Review review);
	
	Review updateReview(Long id, Review review);
	
	boolean deleteReview(Long id);
}
