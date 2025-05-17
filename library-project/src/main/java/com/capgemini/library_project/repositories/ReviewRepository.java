package com.capgemini.library_project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.capgemini.library_project.entities.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query("SELECT r FROM Review r WHERE r.book.bookId = :bookId")
	List<Review> findByBookId(@Param("bookId") Long bookId);

	@Query("SELECT r FROM Review r WHERE r.user.userId = :userId")
	List<Review> findByUserId(@Param("userId") Long userId);

	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.bookId = :bookId")
	Double findAverageRatingByBookId(@Param("bookId") Long bookId);

	@Query("SELECT r FROM Review r WHERE r.rating >= :minRating")
	List<Review> findReviewsWithMinRating(@Param("minRating") int minRating);

	@Query("SELECT COUNT(r) FROM Review r WHERE r.book.bookId = :bookId")
	Long countReviewsByBookId(@Param("bookId") Long bookId);
}
