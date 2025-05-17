package com.capgemini.library_project.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.BorrowRecord;
import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.exceptions.AlreadyReturnedException;
import com.capgemini.library_project.exceptions.InvalidBorrowDateException;
import com.capgemini.library_project.exceptions.InvalidStatusException;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.BorrowRecordRepository;
import com.capgemini.library_project.repositories.UserRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BorrowRecordServicesImpl implements BorrowRecordServices {
	
	private static final String STATUS_BORROWED = "Borrowed";
	private static final String STATUS_RETURNED = "Returned";
	private static final String STATUS_OVERDUE = "Overdue";

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
	@Transactional
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

	    if (book.getAvailableCopies() <= 0) {
	        logger.error("No available copies for book ID {}", bookId);
	        throw new RuntimeException("No available copies for book ID: " + bookId);
	    }

	    book.setAvailableCopies(book.getAvailableCopies() - 1);
	    bookRepository.save(book); //

	    borrowRecord.setUser(user);
	    borrowRecord.setBook(book);
	    borrowRecord.setBorrowStatus(STATUS_BORROWED);
	    borrowRecord.setFine(0);

	    if (borrowRecord.getBorrowDate() == null) {
	        borrowRecord.setBorrowDate(LocalDate.now());
	    }
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
		
		  if (!status.equalsIgnoreCase(STATUS_RETURNED) && !status.equalsIgnoreCase(STATUS_BORROWED) && !status.equalsIgnoreCase(STATUS_OVERDUE)) {
	            throw new InvalidStatusException("Invalid borrow status: " + status);
	        }
		return borrowRecordRepository.findAllByBorrowStatus(status);
	}

	// get all borrow records that are overdue
	@Override
	public List<BorrowRecord> getAllOverdueRecords() {
	    logger.info("Fetching all overdue borrow records");
	    LocalDate today = LocalDate.now();
	    return borrowRecordRepository.findAll().stream()
	            .filter(brecord -> brecord.getBorrowReturnDate() != null
	                    && brecord.getBorrowReturnDate().isBefore(today)
	                    && STATUS_BORROWED.equalsIgnoreCase(brecord.getBorrowStatus()))
	            .toList(); // simpler and more modern
	}

	// quick mark as returned
	@Override
	@Transactional
	public BorrowRecord markAsReturned(Long borrowId) {
		logger.info("Marking borrow record as returned for ID {}", borrowId);
		BorrowRecord brecord = getBorrowRecordById(borrowId);
		
		if (STATUS_RETURNED.equalsIgnoreCase(brecord.getBorrowStatus())) {
            throw new AlreadyReturnedException("Book is already returned for borrow ID: " + borrowId);
        }
		brecord.setBorrowStatus(STATUS_RETURNED);
		brecord.setBorrowReturnDate(LocalDate.now());

		LocalDate dueDate = brecord.getBorrowDate().plusDays(allowedReturnDays);
		if (brecord.getBorrowReturnDate().isAfter(dueDate)) {
			long overdueDays = ChronoUnit.DAYS.between(dueDate, brecord.getBorrowReturnDate());
			int fine = (int) (overdueDays * finePerDay);
			brecord.setFine(fine);
			logger.info("Fine calculated for overdue: {} days, fine: {}", overdueDays, fine);
		} else {
			brecord.setFine(0);
		}

		return borrowRecordRepository.save(brecord);
	}

	// fine based on return date
	@Override
	public Integer calculateFine(Long borrowId) {
		logger.info("Calculating fine for borrow record ID {}", borrowId);
		BorrowRecord brecord = borrowRecordRepository.findById(borrowId)
				.orElseThrow(() -> {
					logger.error("Borrow record not found for fine calculation");
					return new RuntimeException("Record not found");
				});

		LocalDate dueDate = brecord.getBorrowDate().plusDays(allowedReturnDays);
		LocalDate returnDate = brecord.getBorrowReturnDate() != null ? brecord.getBorrowReturnDate() : LocalDate.now();

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
		
		 
	        if (status != null && !status.equalsIgnoreCase(STATUS_BORROWED) &&
	            !status.equalsIgnoreCase(STATUS_RETURNED) &&
	            !status.equalsIgnoreCase(STATUS_OVERDUE)) {
	            throw new InvalidStatusException("Invalid borrow status: " + status);
	        }

		logger.info("Counting borrow records with status: {}", status);
		return borrowRecordRepository.countByBorrowStatus(status);
	}

	// manually update all the details of any issue
	@Override
	public BorrowRecord updateBorrowRecord(Long borrowId, BorrowRecord updatedBorrowRecord) {
		logger.info("Updating borrow record ID {}", borrowId);
		BorrowRecord borrowRecord = getBorrowRecordById(borrowId);
		
		String status = updatedBorrowRecord.getBorrowStatus();
        if (status != null && !status.equalsIgnoreCase(STATUS_BORROWED) &&
            !status.equalsIgnoreCase(STATUS_RETURNED) &&
            !status.equalsIgnoreCase(STATUS_OVERDUE)) {
            throw new InvalidStatusException("Invalid borrow status: " + status);
        }

        if (updatedBorrowRecord.getBorrowReturnDate() != null &&
            updatedBorrowRecord.getBorrowReturnDate().isBefore(borrowRecord.getBorrowDate())) {
            throw new InvalidBorrowDateException("Return date cannot be before borrow date");
        }
        
		borrowRecord.setBorrowStatus(updatedBorrowRecord.getBorrowStatus());
		borrowRecord.setBorrowReturnDate(updatedBorrowRecord.getBorrowReturnDate());
		borrowRecord.setFine(updatedBorrowRecord.getFine());
		return borrowRecordRepository.save(borrowRecord);
	}

	// delete a borrow record
	@Override
	public void deleteBorrowRecord(Long borrowId) {
	    logger.info("Deleting borrow record with ID {}", borrowId);
	    getBorrowRecordById(borrowId);
	    borrowRecordRepository.deleteById(borrowId);
	}

}