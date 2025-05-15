package com.capgemini.library_project.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.BorrowRecord;
import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.BorrowRecordRepository;
import com.capgemini.library_project.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BorrowRecordServicesImpl implements BorrowRecordServices {

	private static final Logger logger = LoggerFactory.getLogger(BorrowRecordServicesImpl.class);

	private final BorrowRecordRepository borrowRecordRepository;
	private final UserRepository userRepository;
	private final BookRepository bookRepository;

	@Value("${borrow.return.days}")
	public int allowedReturnDays;

	@Value("${borrow.fine.per.day}")
	public int finePerDay;

	@Autowired
	public BorrowRecordServicesImpl(BorrowRecordRepository borrowRecordRepository, UserRepository userRepository,
			BookRepository bookRepository) {
		this.borrowRecordRepository = borrowRecordRepository;
		this.userRepository = userRepository;
		this.bookRepository = bookRepository;
	}

	// issue a book
	@Override
	public BorrowRecord createBorrowRecord(BorrowRecord borrowRecord) {
		Long userId = borrowRecord.getUser().getUserId();
		Long bookId = borrowRecord.getBook().getBookId();

		logger.info("Creating borrow record for user ID {} and book ID {}", userId, bookId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> {
					logger.error("User not found with ID {}", userId);
					return new RuntimeException("User not found with ID: " + userId);
				});

		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> {
					logger.error("Book not found with ID {}", bookId);
					return new RuntimeException("Book not found with ID: " + bookId);
				});

		borrowRecord.setUser(user);
		borrowRecord.setBook(book);
		borrowRecord.setBorrowStatus("Borrowed");
		borrowRecord.setFine(0);

		if (borrowRecord.getBorrowReturnDate() == null) {
			borrowRecord.setBorrowReturnDate(borrowRecord.getBorrowDate().plusDays(7));
		}

		logger.debug("Borrow record created: {}", borrowRecord);
		return borrowRecordRepository.save(borrowRecord);
	}

	// display all issued book records
	@Override
	public List<BorrowRecord> getAllBorrowRecord() {
		logger.info("Fetching all borrow records");
		return borrowRecordRepository.findAll();
	}

	// display a single issue by borrowId
	@Override
	public BorrowRecord getBorrowRecordById(Long borrowId) {
		logger.info("Fetching borrow record with ID: {}", borrowId);
		return borrowRecordRepository.findById(borrowId)
				.orElseThrow(() -> {
					logger.error("No borrow record available for ID {}", borrowId);
					return new RuntimeException("No Borrow Record available for " + borrowId);
				});
	}

	// display all issue records of a single user by userId
	@Override
	public List<BorrowRecord> getAllBorrowRecordByUser(Long userId) {
		logger.info("Fetching all borrow records for user ID {}", userId);
		return borrowRecordRepository.findAllByUser_UserId(userId);
	}

	// how many times a book was borrowed
	@Override
	public List<BorrowRecord> getAllBorrowRecordByBook(Long bookId) {
		logger.info("Fetching all borrow records for book ID {}", bookId);
		return borrowRecordRepository.findAllByBook_BookId(bookId);
	}

	// Show all "Returned" or "Overdue" records
	@Override
	public List<BorrowRecord> getBorrowRecordsByStatus(String status) {
		logger.info("Fetching all borrow records with status: {}", status);
		return borrowRecordRepository.findAllByBorrowStatus(status);
	}

	// get all borrow records that are overdue
	@Override
	public List<BorrowRecord> getAllOverdueRecords() {
		logger.info("Fetching all overdue borrow records");
		LocalDate today = LocalDate.now();
		return borrowRecordRepository.findAll().stream()
				.filter(record -> record.getBorrowReturnDate() != null
						&& record.getBorrowReturnDate().isBefore(today)
						&& "Borrowed".equalsIgnoreCase(record.getBorrowStatus()))
				.collect(Collectors.toList());
	}

	// quick mark as returned
	@Override
	public BorrowRecord markAsReturned(Long borrowId) {
		logger.info("Marking borrow record as returned for ID {}", borrowId);
		BorrowRecord record = getBorrowRecordById(borrowId);
		record.setBorrowStatus("Returned");
		record.setBorrowReturnDate(LocalDate.now());

		LocalDate dueDate = record.getBorrowDate().plusDays(allowedReturnDays);
		if (record.getBorrowReturnDate().isAfter(dueDate)) {
			long overdueDays = ChronoUnit.DAYS.between(dueDate, record.getBorrowReturnDate());
			int fine = (int) (overdueDays * finePerDay);
			record.setFine(fine);
			logger.info("Fine calculated for overdue: {} days, fine: {}", overdueDays, fine);
		} else {
			record.setFine(0);
		}

		return borrowRecordRepository.save(record);
	}

	// fine based on return date
	@Override
	public Integer calculateFine(Long borrowId) {
		logger.info("Calculating fine for borrow record ID {}", borrowId);
		BorrowRecord record = borrowRecordRepository.findById(borrowId)
				.orElseThrow(() -> {
					logger.error("Borrow record not found for fine calculation");
					return new RuntimeException("Record not found");
				});

		LocalDate dueDate = record.getBorrowDate().plusDays(allowedReturnDays);
		LocalDate returnDate = record.getBorrowReturnDate() != null ? record.getBorrowReturnDate() : LocalDate.now();

		if (returnDate.isAfter(dueDate)) {
			long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
			int fine = (int) (overdueDays * finePerDay);
			logger.info("Overdue fine: {} days x {} = {}", overdueDays, finePerDay, fine);
			return fine;
		}
		return 0;
	}

	// Count Records by Status (like "Returned", "Borrowed")
	@Override
	public long countBorrowRecordsByStatus(String status) {
		logger.info("Counting borrow records with status: {}", status);
		return borrowRecordRepository.countByBorrowStatus(status);
	}

	// manually update all the details of any issue
	@Override
	public BorrowRecord updateBorrowRecord(Long borrowId, BorrowRecord updatedBorrowRecord) {
		logger.info("Updating borrow record ID {}", borrowId);
		BorrowRecord borrowRecord = getBorrowRecordById(borrowId);
		borrowRecord.setBorrowStatus(updatedBorrowRecord.getBorrowStatus());
		borrowRecord.setBorrowReturnDate(updatedBorrowRecord.getBorrowReturnDate());
		borrowRecord.setFine(updatedBorrowRecord.getFine());
		return borrowRecordRepository.save(borrowRecord);
	}

	// delete a borrow record
	@Override
	public void deleteBorrowRecord(Long borrowId) {
		logger.info("Deleting borrow record with ID {}", borrowId);
		BorrowRecord record = getBorrowRecordById(borrowId);
		borrowRecordRepository.deleteById(borrowId);
	}
}
