package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.repositories.AuthorRepository;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServicesImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookServicesImpl bookServices;

    private Book book;
    private Category category;
    private Author author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new Category();
        category.setCategoryId(1L);
        category.setBooks(new ArrayList<>());

        author = new Author();
        author.setAuthorId(1L);
        author.setBooks(new ArrayList<>());

        book = new Book();
        book.setBookId(1L);
        book.setBookTitle("Test Book");
        book.setTotalCopies(5L);
        book.setAvailableCopies(5L);
    }

    @Test
    void testAddBook() {
        when(bookRepository.save(book)).thenReturn(book);
        Book result = bookServices.addBook(book);
        assertEquals(book, result);
    }

    @Test
    void testAddBookWithCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookServices.addBook(1L, book);
        assertEquals(category, result.getCategory());
        verify(bookRepository).save(book);
    }

    @Test
    void testAssignBook() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookServices.assignBook(1L, 1L);
        assertEquals(category, book.getCategory());
        verify(categoryRepository).save(category);
    }

    @Test
    void testAddBookToAuthor() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookServices.addBookToAuthor(1L, book);
        assertEquals(author, result.getAuthor());
        verify(bookRepository).save(book);
    }

    @Test
    void testAssignBookToAuthor() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookServices.assignBookToAuthor(1L, 1L);
        assertEquals(author, book.getAuthor());
        verify(authorRepository).save(author);
    }

    @Test
    void testUpdateBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.save(book)).thenReturn(book);

        Book updated = bookServices.updateBook(1L, book);
        assertEquals(book, updated);
        verify(bookRepository).save(book);
    }

    @Test
    void testDeleteBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        bookServices.deleteBook(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void testGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookServices.getBookById(1L);
        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = Arrays.asList(book);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookServices.getAllBooks();
        assertEquals(books, result);
    }

    @Test
    void testGetBooksByAuthorId() {
        List<Book> books = Arrays.asList(book);
        when(bookRepository.findByAuthor_AuthorId(1L)).thenReturn(books);

        List<Book> result = bookServices.getBooksByAuthorId(1L);
        assertEquals(books, result);
    }

    @Test
    void testUpdateImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "dummy image".getBytes());

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book updated = bookServices.updateImage(1L, file);
        assertNotNull(updated.getBookCover());
    }

    @Test
    void testGetImage() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookServices.getImage(1L);
        assertEquals(book, result);
    }
}