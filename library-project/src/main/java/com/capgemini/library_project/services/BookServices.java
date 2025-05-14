package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Book;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface BookServices {
	Book addBook(Book book);

	Book updateBook(Long bookId, Book book);

	void deleteBook(Long bookId);

	Optional<Book> getBookById(Long bookId);

	List<Book> getAllBooks();

	List<Book> getBooksByAuthorId(Long authorId);

//	List<Book> getBooksByCategoryId(Long categoryId);

	Book updateImage(Long bookId, MultipartFile image) throws IOException;

	public Book getImage(Long bookId);

	Book addBook(Long categoryId, Book book);
	
	void assignBook(Long categoryId, Long bookId);
}
