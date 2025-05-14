package com.capgemini.library_project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.library_project.entities.Author;


@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
	
	Author findByAuthorName(String authorName);
}
