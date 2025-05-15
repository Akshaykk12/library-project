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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewControllerTest {

    @Mock
    private ReviewServices reviewServices;

    @InjectMocks
    private ReviewController reviewController;

    private Review sampleReview;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleReview = new Review();
        sampleReview.setReviewId(1L);
        sampleReview.setFeedback("Excellent");
        sampleReview.setRating(5);
    }

    @Test
    public void testGetAllReviews() {
        when(reviewServices.getAllReviews()).thenReturn(Arrays.asList(sampleReview));

        ResponseEntity<List<Review>> response = reviewController.getAllReviews();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Excellent", response.getBody().get(0).getFeedback());
    }

    @Test
    public void testGetReviewById() {
        when(reviewServices.getReviewById(1L)).thenReturn(sampleReview);

        ResponseEntity<Review> response = reviewController.getReviewById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Excellent", response.getBody().getFeedback());
    }

    @Test
    public void testCreateReview() {
        when(reviewServices.createReview(sampleReview)).thenReturn(sampleReview);

        ResponseEntity<Review> response = reviewController.createReview(sampleReview);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Excellent", response.getBody().getFeedback());
    }

    @Test
    public void testUpdateReview_Found() {
        when(reviewServices.updateReview(eq(1L), any(Review.class))).thenReturn(sampleReview);

        ResponseEntity<Review> response = reviewController.updateReview(1L, sampleReview);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Excellent", response.getBody().getFeedback());
    }

    @Test
    public void testUpdateReview_NotFound() {
        when(reviewServices.updateReview(eq(2L), any(Review.class))).thenReturn(null);

        ResponseEntity<Review> response = reviewController.updateReview(2L, sampleReview);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteReview() {
        doNothing().when(reviewServices).deleteReview(1L);

        ResponseEntity<Review> response = reviewController.deleteCourse(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
