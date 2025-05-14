package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookServicesImpl implements BookServices {

	private final String UPLOAD_DIR = "uploads/";

	@Autowired
	private BookRepository bookRepository;

	@Override
	public Book addBook(Book book) {
		return bookRepository.save(book);
	}

	@Override
	public Book updateBook(Long bookId, Book book) {
		if (!bookRepository.existsById(bookId)) {
			throw new RuntimeException("Book with ID " + bookId + " not found.");
		}
		book.setBookId(bookId);
		return bookRepository.save(book);
	}

	@Override
	public void deleteBook(Long bookId) {
		if (!bookRepository.existsById(bookId)) {
			throw new RuntimeException("Book with ID " + bookId + " not found.");
		}
		bookRepository.deleteById(bookId);
	}

	@Override
	public Optional<Book> getBookById(Long bookId) {
		return bookRepository.findById(bookId);
	}

	@Override
	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	@Override
	public List<Book> getBooksByAuthorId(Long authorId) {
		return bookRepository.findByAuthorId(authorId);
	}

	@Override
	public List<Book> getBooksByCategoryId(Long categoryId) {
		return bookRepository.findByCategoryId(categoryId);
	}

	@Override
	public Book updateImage(Long bookId, MultipartFile image) throws IOException {
		// Ensure the upload directory exists
		Files.createDirectories(Paths.get(UPLOAD_DIR));

		// Generate a unique file name
		String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
		Path filePath = Paths.get(UPLOAD_DIR, fileName);

		// Save the uploaded image file
		Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		// Find the existing user
		Book user = bookRepository.findById(bookId)
				.orElseThrow(() -> new RuntimeException("User with id " + bookId + " not found"));

		// Update the user's image path
		user.setBookCover(fileName);

		// Save and return the updated user
		return bookRepository.save(user);
	}

	@Override
	public Book getImage(Long bookId) {
		return bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException());
	}
}
