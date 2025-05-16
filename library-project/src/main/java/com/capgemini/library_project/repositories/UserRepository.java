package com.capgemini.library_project.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.capgemini.library_project.entities.User;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long>{

	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.userImage = :image WHERE u.id = :userId")
	int updateImage(@Param("image") String image, @Param("userId") Long userId);
	
	Optional<User> findByUserEmail(String email);
	
	Optional<User> findByUserNameOrUserEmail(String username, String email);
	
	boolean existsByUserEmail(String email);
}