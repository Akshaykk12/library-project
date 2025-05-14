package com.capgemini.library_project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.library_project.entities.Review;
import com.capgemini.library_project.services.ReviewServices;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
	private final ReviewServices reviewServices;
	
	@Autowired
	public ReviewController(ReviewServices reviewServices) {
		this.reviewServices = reviewServices;
	}
	
	@GetMapping
	public ResponseEntity<List<Review>> getAllReviews(){
		return ResponseEntity.status(HttpStatus.OK).body(reviewServices.getAllReviews());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Review> getReviewById(@PathVariable Long id){
		return ResponseEntity.status(HttpStatus.OK).body(reviewServices.getReviewById(id));
	}
	
	@PostMapping
	public ResponseEntity<Review> createReview(@RequestBody Review review){
		Review saved = reviewServices.createReview(review);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review newReview){
		Review updated = reviewServices.updateReview(id, newReview);
		if(updated != null) {
			return ResponseEntity.status(HttpStatus.OK).body(updated);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Review> deleteCourse(@PathVariable Long id){
		reviewServices.deleteReview(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
