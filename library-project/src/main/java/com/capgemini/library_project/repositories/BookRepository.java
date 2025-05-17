package com.capgemini.library_project.repositories;

import com.capgemini.library_project.entities.Book;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
	
	@Modifying
	@Transactional
	@Query("UPDATE Book b SET b.bookCover = :image WHERE b.id = :bookId")
	int updateImage(@Param("image") String image, @Param("bookId") Long bookId);
	
	List<Book> findByAuthor_AuthorId(Long authorId);
	
	Optional<List<Book>> findByCategory_CategoryName(String categoryName);
	
	@Query("SELECT b FROM Book b WHERE LOWER(b.bookTitle) LIKE LOWER(CONCAT('%', :title, '%'))")
	Optional<List<Book>> findByBookTitle(@Param("title") String title);
	
	@Query("SELECT b FROM Book b WHERE LOWER(b.author.authorName) = LOWER(:authorName)")
	Optional<List<Book>> findByAuthorName(@Param("authorName") String authorName);
	
	//for display books and user page 
	@Query("SELECT b, b.author FROM Book b")
	List<Object[]> joinAuthorToBook();
	
	
	//for admin page and for user display
	@Query("SELECT b.category.categoryName, COUNT(b) " +
		       "FROM Book b " +
		       "GROUP BY b.category.categoryName " +
		       "ORDER BY COUNT(b) DESC")
		List<Object[]> findCategoryCount();
			
	
	//finding issue cont of each book form display  
	@Query("SELECT b.bookId, b.author.authorName, b.bookTitle, b.category.categoryName, b.availableCopies, COUNT(br) " +
			       "FROM Book b " +
			       "LEFT JOIN b.borrowRecords br " +
			       "GROUP BY b.bookId, b.author.authorName, b.bookTitle, b.category.categoryName, b.availableCopies")
			List<Object[]> trendingBooksByCategory();


	// for admin page report -gettig most borrowed book
//	@NativeQuery("select b.book_id  , b.title , b.genre , COUNT(br.book_id) AS issue_count from book b LEFT JOIN borrow_record br on b.book_id = br.book_id GROUP BY b.book_id , b.title , b.genre  ;")
			@Query("SELECT b.bookId, b.author.authorName, b.bookTitle, b.category.categoryName, b.availableCopies, COUNT(br) as issueCount " +
				       "FROM Book b " +
				       "LEFT JOIN b.borrowRecords br " +
				       "GROUP BY b.bookId, b.author.authorName, b.bookTitle, b.category.categoryName, b.availableCopies " +
				       "ORDER BY issueCount DESC")
				List<Object[]> topBorrowedBooks();

}
