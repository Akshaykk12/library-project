package com.capgemini.library_project.controllers;

import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.services.BookServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

	
	private BookServices bookService;
	private BookRepository bookRepository;
	
	
	@Autowired
	public BookController(BookServices bookService) {
		super();
		this.bookRepository = bookRepository;
		this.bookService = bookService;
	}

	@PostMapping
	public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
		Book savedBook = bookService.addBook(book);
		return ResponseEntity.ok(savedBook);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @Valid @RequestBody Book book) {
		Book updatedBook = bookService.updateBook(id, book);
		return ResponseEntity.ok(updatedBook);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteBook(@PathVariable("id") Long id) {
		bookService.deleteBook(id);
		return ResponseEntity.ok("Book deleted successfully");
	}

	@GetMapping("/{id}")
	public ResponseEntity<Book> getBookById(@PathVariable("id") Long id) {
		Optional<Book> book = bookService.getBookById(id);
		return book.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<List<Book>> getAllBooks() {
		return ResponseEntity.ok(bookService.getAllBooks());
	}

	@GetMapping("/author/{authorId}")
	public ResponseEntity<List<Book>> getBooksByAuthorId(@PathVariable("authorId") Long authorId) {
		return ResponseEntity.ok(bookService.getBooksByAuthorId(authorId));
	}

//	@GetMapping("/category/{categoryId}")
//	public ResponseEntity<List<Book>> getBooksByCategoryId(@PathVariable("categoryId") Long categoryId) {
//		return ResponseEntity.ok(bookService.getBooksByCategoryId(categoryId));
//	}
	
	@PostMapping("/{categoryId}/assign/{bookId}")
	public ResponseEntity<Void> assignBook(@PathVariable("categoryId") Long categoryId,@PathVariable("bookId") Long bookId){
		bookService.assignBook(categoryId, bookId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/{categoryId}/enroll")
	public ResponseEntity<Book> assignBook(@PathVariable("categoryId") Long categoryId, @RequestBody Book book){
		return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(categoryId, book));
	}
	

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/profile")
	public ResponseEntity<Book> uploadImage(@RequestParam Long bookId, @RequestParam MultipartFile image)
			throws IOException {
		Book saved = bookService.updateImage(bookId, image);
		return ResponseEntity.status(HttpStatus.OK).body(saved);
	}

	@GetMapping("/images/{image}")
	public ResponseEntity<Resource> getImage(@PathVariable String image) throws IOException {
		java.nio.file.Path filePath = Paths.get("uploads").resolve(image).normalize();
		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists() && resource.isReadable()) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{bookId}/remove-image")
	public ResponseEntity<Book> deleteProfileImage(@PathVariable("bookId") Long bookId) {
		Book user = bookRepository.findById(bookId).orElseThrow();
		user.setBookCover(null);
		bookRepository.save(user);
		return ResponseEntity.ok().build();
	}
}
