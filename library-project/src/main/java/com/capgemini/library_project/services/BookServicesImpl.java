package com.capgemini.library_project.services;

import com.capgemini.library_project.dto.AdminDashboardDto;
import com.capgemini.library_project.dto.TrendingBookForUserDto;
import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.Category;
import com.capgemini.library_project.exceptions.BookNotFoundException;
import com.capgemini.library_project.repositories.AuthorRepository;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.BorrowRecordRepository;
import com.capgemini.library_project.repositories.CategoryRepository;
import com.capgemini.library_project.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookServicesImpl implements BookServices {

	private final String UPLOAD_DIR = "uploads/";

	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;
	private final AuthorRepository authorRepository;
	private final UserRepository userRepository;
	private final BorrowRecordRepository borrowRecordRepository;

	public BookServicesImpl(BookRepository bookRepository, CategoryRepository categoryRepository, 
			AuthorRepository authorRepository, UserRepository userRepository, BorrowRecordRepository borrowRecordRepository) {
	
		this.bookRepository = bookRepository;
		this.categoryRepository = categoryRepository;
		this.authorRepository = authorRepository;
		this.userRepository = userRepository;
		this.borrowRecordRepository = borrowRecordRepository;
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
				.orElseThrow(() -> new RuntimeException("Author Not Found"));
		book.setAuthor(author);
		author.getBooks().add(book);
		return bookRepository.save(book);
	}

	@Override
	public void assignBookToAuthor(Long authorId, Long bookId) {
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new RuntimeException("Author Not Found"));
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Category Not Found"));
		;
		author.getBooks().add(book);
		book.setAuthor(author);
		authorRepository.save(author);

	}

	@Override
	public Book updateBook(Long bookId, Book book) {
		if (!bookRepository.existsById(bookId)) {
			throw new BookNotFoundException("Book with ID " + bookId + " not found.");
		}
		book.setBookId(bookId);
		return bookRepository.save(book);
	}

	@Override
	public void deleteBook(Long bookId) {
		if (!bookRepository.existsById(bookId)) {
			throw new BookNotFoundException("Book with ID " + bookId + " not found.");
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
				.orElseThrow(() -> new BookNotFoundException("Book with id " + bookId + " not found"));

		// Update the user's image path
		user.setBookCover(fileName);

		// Save and return the updated user
		return bookRepository.save(user);
	}

	@Override
	public Book getImage(Long bookId) {
		return bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book Not Found"));
	}
	
	@Override
	public Map<String,Long> findCategoryCount() {
		return bookRepository.findCategoryCount().stream().collect(Collectors.toMap(ele -> (String) ele[0],ele -> ((Long) ele[1])));
	}

	@Override
	public AdminDashboardDto dashBoardDto() {
		
		for (Object[] row : bookRepository.findCategoryCount()) {
		    System.out.println("genre: " + row[0].getClass() + ", count: " + row[1].getClass());
		}

		
		Integer authorCount = authorRepository.findAll().size();
		Integer bookCount = bookRepository.findAll().size();
		Integer userCount = userRepository.findAll().size();
		Integer issueCount =  borrowRecordRepository.findAll().stream().filter(rec-> rec.getBorrowStatus()=="Issued").collect(Collectors.toList()).size();
		Integer overdueCount =  borrowRecordRepository.findAll().stream().filter(rec-> rec.getBorrowStatus()=="Overdue").collect(Collectors.toList()).size();
		 Map<String, Long> genreCount = bookRepository.findCategoryCount().stream().collect(Collectors.toMap(ele -> (String) ele[0],ele -> ((Long) ele[1])));
		 List<TrendingBookForUserDto> topBooksCount =  topBorrowedBooks(); 
		return new AdminDashboardDto(authorCount,bookCount,userCount,issueCount,overdueCount,genreCount,topBooksCount);
	}



	@Override
	public List<TrendingBookForUserDto> getTrendingBooksForUser() {
		List<Object[]> rawResult = bookRepository.trendingBooksByCategory();

		// 1 bookId
		//  authorName
		//   title
		//  genre
		//  availableCopies
		//  issueCount
	    return rawResult.stream().map(obj -> 
	        new TrendingBookForUserDto(
	            ((Long) obj[0]),  
	            (String) obj[1],
	            (String) obj[2],  
	            (String) obj[3],    
	            ((Long) obj[4]),    
	            ((Long) obj[5])     
	        )
	    ).collect(Collectors.toList());
	}



	@Override
	public List<TrendingBookForUserDto> topBorrowedBooks() {
		List<Object[]> rawResult = bookRepository.topBorrowedBooks();
		
		//just added order by ofr desc sorting by issue cnt
		
		return rawResult.stream().map(obj -> 
        new TrendingBookForUserDto(
        		((Long) obj[0]),  
	            (String) obj[1],
	            (String) obj[2],  
	            (String) obj[3],    
	            ((Long) obj[4]),    
	            ((Long) obj[5])    
        )
    ).collect(Collectors.toList());
	}
	
}
