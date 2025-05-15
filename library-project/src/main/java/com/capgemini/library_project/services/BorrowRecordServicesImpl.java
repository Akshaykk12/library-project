package com.capgemini.library_project.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.BorrowRecord;
import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.BorrowRecordRepository;
import com.capgemini.library_project.repositories.UserRepository;

@Service
public class BorrowRecordServicesImpl implements BorrowRecordServices {

	private final BorrowRecordRepository borrowRecordRepository;
	private final UserRepository userRepository;
	private final BookRepository bookRepository;

	@Value("${borrow.return.days}")
	private int allowedReturnDays;

	@Value("${borrow.fine.per.day}")
	private int finePerDay;

	@Autowired
	public BorrowRecordServicesImpl(BorrowRecordRepository borrowRecordRepository, UserRepository userRepository,
			BookRepository bookRepository) {
		this.borrowRecordRepository = borrowRecordRepository;
		this.userRepository = userRepository;
		this.bookRepository = bookRepository;
	}

	@Override
	public BorrowRecord createBorrowRecord(BorrowRecord borrowRecord) {
	    Long userId = borrowRecord.getUser().getUserId();
	    Long bookId = borrowRecord.getBook().getBookId();

	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

	    Book book = bookRepository.findById(bookId)
	            .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

	    borrowRecord.setUser(user);
	    borrowRecord.setBook(book);
	    borrowRecord.setBorrowStatus("Borrowed");
	    borrowRecord.setFine(0);

	    if (borrowRecord.getBorrowReturnDate() == null) {
	        borrowRecord.setBorrowReturnDate(borrowRecord.getBorrowDate().plusDays(7));
	    }

	    return borrowRecordRepository.save(borrowRecord);
	}

	@Override
	public List<BorrowRecord> getAllBorrowRecord() {
		return borrowRecordRepository.findAll();
	}

	@Override
	public BorrowRecord getBorrowRecordById(Long borrowId) {
		return borrowRecordRepository.findById(borrowId)
				.orElseThrow(() -> new RuntimeException("No Borrow Record available for " + borrowId));
	}

	@Override
	public List<BorrowRecord> getAllBorrowRecordByUser(Long userId) {
		return borrowRecordRepository.findAllByUser_UserId(userId);
	}

	// how many times a book was borrowed
	@Override
	public List<BorrowRecord> getAllBorrowRecordByBook(Long bookId) {
		return borrowRecordRepository.findAllByBook_BookId(bookId);
	}

	// Show all "Returned" or "Overdue" records
	public List<BorrowRecord> getBorrowRecordsByStatus(String status) {
		return borrowRecordRepository.findAllByBorrowStatus(status);
	}

	// get all borrow records that are overdue
	@Override
	public List<BorrowRecord> getAllOverdueRecords() {
		LocalDate today = LocalDate.now();
		return borrowRecordRepository.findAll().stream()
				.filter(record -> record.getBorrowReturnDate() != null && record.getBorrowReturnDate().isBefore(today)
						&& "Borrowed".equalsIgnoreCase(record.getBorrowStatus()))
				.collect(Collectors.toList());
	}

	// quick mark as returned
	@Override
	public BorrowRecord markAsReturned(Long borrowId) {
		BorrowRecord record = getBorrowRecordById(borrowId);
		record.setBorrowStatus("Returned");
		record.setBorrowReturnDate(LocalDate.now());

		LocalDate dueDate = record.getBorrowDate().plusDays(allowedReturnDays);
		if (record.getBorrowReturnDate().isAfter(dueDate)) {
			long overdueDays = ChronoUnit.DAYS.between(dueDate, record.getBorrowReturnDate());
			int fine = (int) (overdueDays * finePerDay);
			record.setFine(fine);
		} else {
			record.setFine(0);
		}

		return borrowRecordRepository.save(record);
	}

	// fine based on return date
	@Override
	public Integer calculateFine(Long borrowId) {
		BorrowRecord record = borrowRecordRepository.findById(borrowId)
				.orElseThrow(() -> new RuntimeException("Record not found"));

		LocalDate dueDate = record.getBorrowDate().plusDays(allowedReturnDays);
		LocalDate returnDate = record.getBorrowReturnDate() != null ? record.getBorrowReturnDate() : LocalDate.now();

		if (returnDate.isAfter(dueDate)) {
			long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
			return (int) (overdueDays * finePerDay);
		} else {
			return 0;
		}
	}

	// Count Records by Status (like "Returned", "Borrowed")
	@Override
	public long countBorrowRecordsByStatus(String status) {
		return borrowRecordRepository.countByBorrowStatus(status);
	}

	@Override
	public BorrowRecord updateBorrowRecord(Long borrowId, BorrowRecord updatedBorrowRecord) {
		BorrowRecord borrowRecord = getBorrowRecordById(borrowId);
		borrowRecord.setBorrowStatus(updatedBorrowRecord.getBorrowStatus());
		borrowRecord.setBorrowReturnDate(updatedBorrowRecord.getBorrowReturnDate());
		borrowRecord.setFine(updatedBorrowRecord.getFine());
		return borrowRecordRepository.save(borrowRecord);
	}

	@Override
	public void deleteBorrowRecord(Long borrowId) {
		BorrowRecord record = getBorrowRecordById(borrowId);
		borrowRecordRepository.deleteById(borrowId);

	}

}
