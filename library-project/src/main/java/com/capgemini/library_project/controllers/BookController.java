package com.capgemini.library_project.controllers;

import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.services.BookServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    private final BookServices bookService;
    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookServices bookService, BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        logger.info("POST: Adding new book");
        Book savedBook = bookService.addBook(book);
        return ResponseEntity.ok(savedBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @Valid @RequestBody Book book) {
        logger.info("PUT: Updating book with ID {}", id);
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
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        logger.info("GET: Fetching all books");
        return ResponseEntity.ok(bookService.getAllBooks());
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
    public ResponseEntity<Book> assignBook(@PathVariable("categoryId") Long categoryId, @RequestBody Book book) {
        logger.info("POST: Adding book to category ID {}", categoryId);
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
    public ResponseEntity<Book> assignBookToAuthor(@PathVariable("authorId") Long authorId, @RequestBody Book book) {
        logger.info("POST: Adding book to author ID {}", authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBookToAuthor(authorId, book));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/profile")
    public ResponseEntity<Book> uploadImage(@RequestParam Long bookId, @RequestParam MultipartFile image) throws IOException {
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
}
