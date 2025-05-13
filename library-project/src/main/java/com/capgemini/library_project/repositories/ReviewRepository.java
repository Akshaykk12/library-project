package com.capgemini.library_project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capgemini.library_project.entities.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{

}
