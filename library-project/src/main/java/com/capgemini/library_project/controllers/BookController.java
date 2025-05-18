package com.capgemini.library_project.controllers;

import com.capgemini.library_project.dto.AdminDashboardDto;
import com.capgemini.library_project.dto.BookDto;
import com.capgemini.library_project.dto.TrendingBookForUserDto;
import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.services.AuthorServices;
import com.capgemini.library_project.services.BookServices;
import com.capgemini.library_project.services.CategoryServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/books")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class BookController {

	private static final Logger logger = LoggerFactory.getLogger(BookController.class);

	private final BookServices bookService;
	private final BookRepository bookRepository;
	private final CategoryServices categoryServices;
	private final AuthorServices authorServices;

	@Autowired
	public BookController(BookServices bookService, BookRepository bookRepository, AuthorServices authorServices, CategoryServices categoryServices) {
		this.bookRepository = bookRepository;
		this.bookService = bookService;
		this.authorServices = authorServices;
		this.categoryServices = categoryServices;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Book> addBook(@RequestParam String bookTitle, @RequestParam Long totalCopies, @RequestParam Long availableCopies, @RequestParam Long authorId, @RequestParam Long categoryId, @RequestParam("bookCover") MultipartFile bookCover) throws IOException{
		logger.info("POST: Adding new book");
		Author author = authorServices.findAuthorById(authorId);
		Category category = categoryServices.getCategoryById(categoryId);
		Book savedBook = bookService.addBook(bookTitle, totalCopies, availableCopies, author, category, bookCover);
		return ResponseEntity.ok(savedBook);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @Valid @RequestBody Book book,
			BindingResult bindingResult) {
		logger.info("PUT: Updating book with ID {}", id);
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		Book updatedBook = bookService.updateBook(id, book);
		return ResponseEntity.ok(updatedBook);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteBook(@PathVariable("id") Long id) {
		logger.info("DELETE: Deleting book with ID {}", id);
		bookService.deleteBook(id);
		return ResponseEntity.ok("Book deleted successfully");
	}

	@GetMapping("/{id}")
	public ResponseEntity<Book> getBookById(@PathVariable("id") Long id) {
		logger.info("GET: Fetching book with ID {}", id);
		Book book = bookService.getBookById(id);
		//return book.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
		
		if (book != null) {
	        return ResponseEntity.ok(book);
	    } else {
	    	return ResponseEntity.notFound().build();
	    }
	}

	@GetMapping
	public ResponseEntity<List<BookDto>> getAllBooks() {
	    List<Book> books = bookService.getAllBooks();
	    List<BookDto> bookDtos = books.stream().map(book -> {
	        BookDto dto = new BookDto();
	        dto.setBookId(book.getBookId());
	        dto.setBookTitle(book.getBookTitle());
	        dto.setTotalCopies(book.getTotalCopies());
	        dto.setAvailableCopies(book.getAvailableCopies());
	        dto.setBookCover(book.getBookCover());
	        dto.setAuthorName(book.getAuthor() != null ? book.getAuthor().getAuthorName() : null);
	        dto.setCategoryName(book.getCategory() != null ? book.getCategory().getCategoryName() : null);
	        return dto;
	    }).toList();

	    return ResponseEntity.ok(bookDtos);
	}

	@GetMapping("/author/{authorId}")
	public ResponseEntity<List<Book>> getBooksByAuthorId(@PathVariable("authorId") Long authorId) {
		logger.info("GET: Fetching books by author ID {}", authorId);
		return ResponseEntity.ok(bookService.getBooksByAuthorId(authorId));
	}

	@PostMapping("/{categoryId}/assigncategory/{bookId}")
	public ResponseEntity<Void> assignBook(@PathVariable("categoryId") Long categoryId,
			@PathVariable("bookId") Long bookId) {
		logger.info("POST: Assigning book ID {} to category ID {}", bookId, categoryId);
		bookService.assignBook(categoryId, bookId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{categoryId}/enrollcategory")
	public ResponseEntity<Book> assignBook(@PathVariable("categoryId") Long categoryId, @Valid @RequestBody Book book,
			BindingResult bindingResult) {
		logger.info("POST: Adding book to category ID {}", categoryId);
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(categoryId, book));
	}

	@PostMapping("/{authorId}/assignauthor/{bookId}")
	public ResponseEntity<Void> assignBookToAuthor(@PathVariable("authorId") Long authorId,
			@PathVariable("bookId") Long bookId) {
		logger.info("POST: Assigning book ID {} to author ID {}", bookId, authorId);
		bookService.assignBookToAuthor(authorId, bookId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{authorId}/enrollauthor")
	public ResponseEntity<Book> assignBookToAuthor(@PathVariable("authorId") Long authorId,
			@Valid @RequestBody Book book, BindingResult bindingResult) {
		logger.info("POST: Adding book to author ID {}", authorId);
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBookToAuthor(authorId, book));
	}

	@PostMapping("/createWithAuthorAndCategory")
	public ResponseEntity<Book> createBookWithAuthorAndCategory(@RequestParam Long authorId,
			@RequestParam Long categoryId, @Valid @RequestBody Book book, BindingResult bindingResult) {

		logger.info("POST: Creating book with author ID {} and category ID {}", authorId, categoryId);

		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid book data");
		}

		Book savedBook = bookService.addBook(book);

		bookService.assignBookToAuthor(authorId, savedBook.getBookId());
		bookService.assignBook(categoryId, savedBook.getBookId());

		return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/profile")
	public ResponseEntity<Book> uploadImage(@RequestParam Long bookId, @RequestParam MultipartFile image)
			throws IOException {
		logger.info("POST: Uploading image for book ID {}", bookId);
		Book saved = bookService.updateImage(bookId, image);
		return ResponseEntity.ok(saved);
	}

	@GetMapping("/images/{image}")
	public ResponseEntity<Resource> getImage(@PathVariable String image) throws IOException {
		logger.info("GET: Retrieving image file {}", image);
		java.nio.file.Path filePath = Paths.get("uploads").resolve(image).normalize();
		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists() && resource.isReadable()) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
		} else {
			logger.warn("Requested image {} not found or unreadable", image);
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{bookId}/remove-image")
	public ResponseEntity<Book> deleteProfileImage(@PathVariable("bookId") Long bookId) {
		logger.info("DELETE: Removing image for book ID {}", bookId);
		Book book = bookRepository.findById(bookId).orElseThrow();
		book.setBookCover(null);
		bookRepository.save(book);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/categoryCount")
	public ResponseEntity<List<Object[]>> getCategoryBookCounts() {
		logger.info("GET: Fetching book counts by category");
		return ResponseEntity.ok(bookService.getCategoryBookCounts());
	}

	@GetMapping("/count")
	public ResponseEntity<Long> getTotalBookCount() {
		return ResponseEntity.ok(bookRepository.count());
	}

	@GetMapping("/adminData")
	public ResponseEntity<AdminDashboardDto> getAdminDashBoardData() {
		return ResponseEntity.status(200).body(bookService.dashBoardDto());
	}

	@GetMapping("/userData")
	public ResponseEntity<List<TrendingBookForUserDto>> getUserDisplayData() {
		return ResponseEntity.status(200).body(bookService.getTrendingBooksForUser());
	}
}
