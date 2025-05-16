package com.capgemini.library_project.repositories;

import com.capgemini.library_project.entities.Book;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
	List<Book> findByAuthor_AuthorId(Long authorId);

	@Modifying
	@Transactional
	@Query("UPDATE Book b SET b.bookCover = :image WHERE b.id = :bookId")
	int updateImage(@Param("image") String image, @Param("bookId") Long bookId);
}
