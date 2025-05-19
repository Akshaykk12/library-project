package com.capgemini.library_project.services;

import com.capgemini.library_project.dto.AdminDashboardDto;
import com.capgemini.library_project.dto.TrendingBookForUserDto;
import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.Category;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface BookServices {
	Book addBook(String bookTitle, Long totalCopies, Long availableCopies, Author author, Category category,
			MultipartFile bookCover) throws IOException;

	Book addBook(Book book);

	Book updateBook(Long bookId, Book book);

	void deleteBook(Long bookId);

	Book getBookById(Long bookId);

	List<Book> getAllBooks();

	List<Book> getBooksByAuthorId(Long authorId);

	Book updateImage(Long bookId, MultipartFile image) throws IOException;

	public Book getImage(Long bookId);

	Book addBook(Long categoryId, Book book);

	void assignBook(Long categoryId, Long bookId);

	Book addBookToAuthor(Long authorId, Book book);

	void assignBookToAuthor(Long authorId, Long bookId);

	List<Object[]> getCategoryBookCounts();

	Map<String, Long> findCategoryCount();

	AdminDashboardDto dashBoardDto();

	List<TrendingBookForUserDto> getTrendingBooksForUser();

	List<TrendingBookForUserDto> topBorrowedBooks();
}
