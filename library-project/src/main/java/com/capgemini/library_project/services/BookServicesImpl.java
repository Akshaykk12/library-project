package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.repositories.AuthorRepository;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.CategoryRepository;

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

	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;
	private final AuthorRepository authorRepository;

	public BookServicesImpl(BookRepository bookRepository, CategoryRepository categoryRepository, AuthorRepository authorRepository) {
	
		this.bookRepository = bookRepository;
		this.categoryRepository = categoryRepository;
		this.authorRepository = authorRepository;
	}

	@Override
	public Book addBook(Book book) {
		return bookRepository.save(book);
	}

	@Override
	public Book addBook(Long categoryId, Book book) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category Not Found"));
		book.setCategory(category);
		category.getBooks().add(book);
		return bookRepository.save(book);
	}

	@Override
	public void assignBook(Long categoryId, Long bookId) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category Not Found"));
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Category Not Found"));
		category.getBooks().add(book);
		book.setCategory(category);
		categoryRepository.save(category);

	}

	@Override
	public Book addBookToAuthor(Long authorId, Book book) {
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new RuntimeException("Category Not Found"));
		book.setAuthor(author);
		author.getBooks().add(book);
		return bookRepository.save(book);
	}

	@Override
	public void assignBookToAuthor(Long authorId, Long bookId) {
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new RuntimeException("Category Not Found"));
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Category Not Found"));
		;
		author.getBooks().add(book);
		book.setAuthor(author);
		authorRepository.save(author);

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
		return bookRepository.findByAuthor_AuthorId(authorId);
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
