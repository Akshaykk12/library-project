package com.capgemini.library_project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.capgemini.library_project.entities.BorrowRecord;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

	List<BorrowRecord> findAllByUser_UserId(Long userId);

	// how many times a book was borrowed
	List<BorrowRecord> findAllByBook_BookId(Long bookId);

	// Show all "Returned" or "Overdue" records
	List<BorrowRecord> findAllByBorrowStatus(String status);

	// Count Records by Status (like "Returned", "Borrowed")
	long countByBorrowStatus(String status);

	@Query("SELECT br.book.bookTitle, COUNT(br) as borrowCount " +"FROM BorrowRecord br " + "GROUP BY br.book.bookTitle " +"ORDER BY borrowCount DESC")
		List<Object[]> findTopBorrowedBooks();

		
		@Query("SELECT DATE_FORMAT(br.borrowDate, '%Y-%m') as month, COUNT(br) as count " +"FROM BorrowRecord br " +"GROUP BY DATE_FORMAT(br.borrowDate, '%Y-%m')")
			List<Object[]> countBorrowRecordsByMonth();
			
			List<BorrowRecord> findByBorrowStatusIn(List<String> statuses);
}
