package com.capgemini.library_project.controllers;

import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.services.BookServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookControllerTest {

	@Mock
	private BookServices bookService;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BindingResult bindingResult;

	@InjectMocks
	private BookController bookController;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testAddBook() {
		Book book = new Book();
		when(bindingResult.hasErrors()).thenReturn(false);
		when(bookService.addBook(book)).thenReturn(book);

		ResponseEntity<Book> response = bookController.addBook(book, bindingResult);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(book, response.getBody());
	}

	@Test
	public void testUpdateBook() {
		Book book = new Book();
		when(bindingResult.hasErrors()).thenReturn(false);
		when(bookService.updateBook(1L, book)).thenReturn(book);

		ResponseEntity<Book> response = bookController.updateBook(1L, book, bindingResult);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(book, response.getBody());
	}

	@Test
	public void testDeleteBook() {
		doNothing().when(bookService).deleteBook(1L);

		ResponseEntity<String> response = bookController.deleteBook(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Book deleted successfully", response.getBody());
	}

	@Test
	public void testGetBookByIdFound() {
		Book book = new Book();
		when(bookService.getBookById(1L)).thenReturn((book));

		ResponseEntity<Book> response = bookController.getBookById(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(book, response.getBody());
	}

	@Test
	public void testGetBookByIdNotFound() {
		when(bookService.getBookById(1L)).thenReturn(null);

		ResponseEntity<Book> response = bookController.getBookById(1L);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testGetAllBooks() {
		List<Book> books = Arrays.asList(new Book(), new Book());
		when(bookService.getAllBooks()).thenReturn(books);

		ResponseEntity<List<Book>> response = bookController.getAllBooks();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, response.getBody().size());
	}

	@Test
	public void testGetBooksByAuthorId() {
		List<Book> books = Arrays.asList(new Book(), new Book());
		when(bookService.getBooksByAuthorId(1L)).thenReturn(books);

		ResponseEntity<List<Book>> response = bookController.getBooksByAuthorId(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, response.getBody().size());
	}

	@Test
	public void testAssignBookToCategory() {
		doNothing().when(bookService).assignBook(1L, 2L);

		ResponseEntity<Void> response = bookController.assignBook(1L, 2L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testAssignBookToCategoryAndCreate() {
		Book book = new Book();
		when(bindingResult.hasErrors()).thenReturn(false);
		when(bookService.addBook(1L, book)).thenReturn(book);

		ResponseEntity<Book> response = bookController.assignBook(1L, book, bindingResult);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(book, response.getBody());
	}

	@Test
	public void testAssignBookToAuthor() {
		doNothing().when(bookService).assignBookToAuthor(1L, 2L);

		ResponseEntity<Void> response = bookController.assignBookToAuthor(1L, 2L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testAssignBookToAuthorAndCreate() {
		Book book = new Book();
		when(bindingResult.hasErrors()).thenReturn(false);
		when(bookService.addBookToAuthor(1L, book)).thenReturn(book);

		ResponseEntity<Book> response = bookController.assignBookToAuthor(1L, book, bindingResult);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(book, response.getBody());
	}

	@Test
	public void testUploadImage() throws IOException {
		Book book = new Book();
		MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[10]);
		when(bookService.updateImage(1L, image)).thenReturn(book);

		ResponseEntity<Book> response = bookController.uploadImage(1L, image);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(book, response.getBody());
	}

	@Test
	public void testDeleteProfileImage() {
		Book book = new Book();
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		ResponseEntity<Book> response = bookController.deleteProfileImage(1L);

		verify(bookRepository).save(book);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
