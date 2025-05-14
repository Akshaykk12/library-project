package com.capgemini.library_project.services;

import java.util.List;

import com.capgemini.library_project.entities.BorrowRecord;

import org.springframework.data.domain.Page;

public interface BorrowRecordServices {

	BorrowRecord createBorrowRecord(BorrowRecord borrowRecord);

	List<BorrowRecord> getAllBorrowRecord();

	BorrowRecord getBorrowRecordById(Long borrowId);

	List<BorrowRecord> getAllBorrowRecordByUser(Long userId);

	// how many times a book was borrowed
	List<BorrowRecord> getAllBorrowRecordByBook(Long bookId);

	// show all "Returned" or "Overdue" records
	List<BorrowRecord> getBorrowRecordsByStatus(String status);

	// get all borrow records that are overdue
	List<BorrowRecord> getAllOverdueRecords();

	// quick mark as returned
	BorrowRecord markAsReturned(Long borrowId);

//	// fine based on return date
	Integer calculateFine(Long borrowId);

	// Count Records by Status (like "Returned", "Borrowed")
	long countBorrowRecordsByStatus(String status);

	BorrowRecord updateBorrowRecord(Long borrowId, BorrowRecord updatedBorrowRecord);

	void deleteBorrowRecord(Long borrowId);

}
