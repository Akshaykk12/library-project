package com.capgemini.library_project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.library_project.entities.Review;
import com.capgemini.library_project.repositories.ReviewRepository;

@Service
public class ReviewServicesImpl implements ReviewServices{
	private final ReviewRepository reviewRepository;
	
	@Autowired
	public ReviewServicesImpl(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
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

}
