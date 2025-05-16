package com.capgemini.library_project.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capgemini.library_project.entities.Author;

import jakarta.transaction.Transactional;


@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

	 Optional<Author> findByAuthorName(String authorName);
	
	@Modifying
	@Transactional
	@Query("UPDATE Author a SET a.authorImage = :image WHERE a.id = :authorId")
	int updateImage(@Param("image") String image, @Param("authorId") Long authorId);
}
