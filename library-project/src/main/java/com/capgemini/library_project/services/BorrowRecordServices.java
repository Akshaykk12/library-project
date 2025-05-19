package com.capgemini.library_project.services;

import java.util.List;

import com.capgemini.library_project.dto.BorrowRecordDto;
import com.capgemini.library_project.entities.BorrowRecord;

public interface BorrowRecordServices {

	List<BorrowRecord> getAllBorrowRecord();

	BorrowRecord getBorrowRecordById(Long borrowId);

	List<BorrowRecordDto> getAllBorrowRecordByUser(Long userId);

	List<BorrowRecord> getAllBorrowRecordByBook(Long bookId);

	List<BorrowRecord> getBorrowRecordsByStatus(String status);

	List<BorrowRecord> getAllOverdueRecords();

	BorrowRecord markAsReturned(Long borrowId);

	Integer calculateFine(Long borrowId);

	long countBorrowRecordsByStatus(String status);

	BorrowRecord updateBorrowRecord(Long borrowId, BorrowRecord updatedBorrowRecord);

	void deleteBorrowRecord(Long borrowId);

	BorrowRecord borrowBook(BorrowRecordDto dto);

	List<Object[]> findTopBorrowedBooks();

	List<Object[]> getMonthlyBorrowCounts();

	List<BorrowRecord> getIssuedRecords();

	BorrowRecord updateStatus(Long borrowId, String status);

	long countActiveBorrows();

	long countOverdueRecords();
}
