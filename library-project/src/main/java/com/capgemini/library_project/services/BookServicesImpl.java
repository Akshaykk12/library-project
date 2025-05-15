package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.repositories.AuthorRepository;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.CategoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookServicesImpl implements BookServices {

    private static final Logger logger = LoggerFactory.getLogger(BookServicesImpl.class);
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
        logger.info("Saving new book: {}", book.getBookTitle());
        return bookRepository.save(book);
    }

    @Override
    public Book addBook(Long categoryId, Book book) {
        logger.info("Adding book to category ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.error("Category not found with ID: {}", categoryId);
                    return new RuntimeException("Category Not Found");
                });
        book.setCategory(category);
        category.getBooks().add(book);
        return bookRepository.save(book);
    }

    @Override
    public void assignBook(Long categoryId, Long bookId) {
        logger.info("Assigning book ID {} to category ID {}", bookId, categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.error("Category not found with ID: {}", categoryId);
                    return new RuntimeException("Category Not Found");
                });
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Book not found with ID: {}", bookId);
                    return new RuntimeException("Category Not Found");
                });
        category.getBooks().add(book);
        book.setCategory(category);
        categoryRepository.save(category);
    }

    @Override
    public Book addBookToAuthor(Long authorId, Book book) {
        logger.info("Adding book to author ID: {}", authorId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> {
                    logger.error("Author not found with ID: {}", authorId);
                    return new RuntimeException("Category Not Found");
                });
        book.setAuthor(author);
        author.getBooks().add(book);
        return bookRepository.save(book);
    }

    @Override
    public void assignBookToAuthor(Long authorId, Long bookId) {
        logger.info("Assigning book ID {} to author ID {}", bookId, authorId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> {
                    logger.error("Author not found with ID: {}", authorId);
                    return new RuntimeException("Category Not Found");
                });
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Book not found with ID: {}", bookId);
                    return new RuntimeException("Category Not Found");
                });
        author.getBooks().add(book);
        book.setAuthor(author);
        authorRepository.save(author);
    }

    @Override
    public Book updateBook(Long bookId, Book book) {
        logger.info("Updating book with ID: {}", bookId);
        if (!bookRepository.existsById(bookId)) {
            logger.error("Book with ID {} not found", bookId);
            throw new RuntimeException("Book with ID " + bookId + " not found.");
        }
        book.setBookId(bookId);
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long bookId) {
        logger.info("Deleting book with ID: {}", bookId);
        if (!bookRepository.existsById(bookId)) {
            logger.error("Book with ID {} not found", bookId);
            throw new RuntimeException("Book with ID " + bookId + " not found.");
        }
        bookRepository.deleteById(bookId);
    }

    @Override
    public Optional<Book> getBookById(Long bookId) {
        logger.info("Fetching book with ID: {}", bookId);
        return bookRepository.findById(bookId);
    }

    @Override
    public List<Book> getAllBooks() {
        logger.info("Fetching all books");
        return bookRepository.findAll();
    }

    @Override
    public List<Book> getBooksByAuthorId(Long authorId) {
        logger.info("Fetching books by author ID: {}", authorId);
        return bookRepository.findByAuthor_AuthorId(authorId);
    }

    @Override
    public Book updateImage(Long bookId, MultipartFile image) throws IOException {
        logger.info("Updating image for book ID: {}", bookId);
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> {
                    logger.error("Book with ID {} not found for image update", bookId);
                    return new RuntimeException("User with id " + bookId + " not found");
                });

        book.setBookCover(fileName);
        return bookRepository.save(book);
    }

    @Override
    public Book getImage(Long bookId) {
        logger.info("Fetching image for book ID: {}", bookId);
        return bookRepository.findById(bookId).orElseThrow(() -> {
            logger.error("Book with ID {} not found when fetching image", bookId);
            return new RuntimeException();
        });
    }
}
