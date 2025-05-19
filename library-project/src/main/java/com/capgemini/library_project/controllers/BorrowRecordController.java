package com.capgemini.library_project.controllers;

import java.util.List;
import java.util.Map;

import com.capgemini.library_project.dto.BorrowRecordDto;
import com.capgemini.library_project.entities.BorrowRecord;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.UserRepository;
import com.capgemini.library_project.services.BorrowRecordServices;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/borrowRecords")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class BorrowRecordController {

	private static final Logger logger = LoggerFactory.getLogger(BorrowRecordController.class);

	private final BorrowRecordServices borrowRecordServices;
	BookRepository bookRepository;
	UserRepository userRepository;

	@Autowired
	public BorrowRecordController(BorrowRecordServices borrowRecordServices, BookRepository bookRepository,
			UserRepository userRepository) {
		this.borrowRecordServices = borrowRecordServices;

		this.bookRepository = bookRepository;
		this.userRepository = userRepository;
	}

	@PostMapping("/borrow")
	public ResponseEntity<BorrowRecord> borrowBook(@Valid @RequestBody BorrowRecordDto dto) {
		logger.info("POST: Borrowing book {} to user {}", dto.getBookId(), dto.getUserId());
		BorrowRecord saved = borrowRecordServices.borrowBook(dto);
		return ResponseEntity.ok(saved);
	}

	@GetMapping
	public ResponseEntity<List<BorrowRecord>> getAllBorrowRecords() {
		logger.info("GET: Fetching all borrow records");
		return ResponseEntity.ok(borrowRecordServices.getAllBorrowRecord());
	}

	@GetMapping("/{borrowId}")
	public ResponseEntity<BorrowRecord> getBorrowRecordById(@PathVariable Long borrowId) {
		logger.info("GET: Fetching borrow record by ID {}", borrowId);
		return ResponseEntity.ok(borrowRecordServices.getBorrowRecordById(borrowId));
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<BorrowRecordDto>> getAllBorrowRecordByUser(@PathVariable Long userId) {
		logger.info("GET: Fetching borrow records for user ID {}", userId);
		return ResponseEntity.ok(borrowRecordServices.getAllBorrowRecordByUser(userId));
	}

	@GetMapping("/book/{bookId}")
	public ResponseEntity<List<BorrowRecord>> getAllBorrowRecordByBook(@PathVariable Long bookId) {
		logger.info("GET: Fetching borrow records for book ID {}", bookId);
		return ResponseEntity.ok(borrowRecordServices.getAllBorrowRecordByBook(bookId));
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<List<BorrowRecord>> getBorrowRecordsByStatus(@PathVariable String status) {
		logger.info("GET: Fetching records by status '{}'", status);
		return ResponseEntity.ok(borrowRecordServices.getBorrowRecordsByStatus(status));
	}

	@GetMapping("/overdue")
	public ResponseEntity<List<BorrowRecord>> getAllOverdueRecords() {
		logger.info("GET: Fetching all overdue borrow records");
		return ResponseEntity.ok(borrowRecordServices.getAllOverdueRecords());
	}

	@PutMapping("/markReturned/{borrowId}")
	public ResponseEntity<BorrowRecord> markAsReturned(@PathVariable Long borrowId) {
		logger.info("PUT: Marking borrow record {} as returned", borrowId);
		BorrowRecord updatedRecord = borrowRecordServices.markAsReturned(borrowId);
		return ResponseEntity.ok(updatedRecord);
	}

	@GetMapping("/calculateFine/{borrowId}")
	public ResponseEntity<Integer> calculateFine(@PathVariable Long borrowId) {
		logger.info("GET: Calculating fine for borrow ID {}", borrowId);
		return ResponseEntity.ok(borrowRecordServices.calculateFine(borrowId));
	}

	@GetMapping("/countByStatus/{status}")
	public ResponseEntity<Long> countByStatus(@PathVariable String status) {
		logger.info("GET: Counting borrow records with status '{}'", status);
		long count = borrowRecordServices.countBorrowRecordsByStatus(status);
		return ResponseEntity.ok(count);
	}

	@PutMapping("/{borrowId}")
	public ResponseEntity<BorrowRecord> updateBorrowRecord(@PathVariable Long borrowId,
			@Valid @RequestBody BorrowRecord updatedBorrowRecord, BindingResult bindingResult) {
		logger.info("PUT: Updating borrow record {}", borrowId);

		return ResponseEntity.ok(borrowRecordServices.updateBorrowRecord(borrowId, updatedBorrowRecord));
	}

	@DeleteMapping("/{borrowId}")
	public ResponseEntity<Void> deleteBorrowRecord(@PathVariable Long borrowId) {
		logger.info("DELETE: Deleting borrow record with ID {}", borrowId);
		borrowRecordServices.deleteBorrowRecord(borrowId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/topBooks")
	public ResponseEntity<List<Object[]>> getTopBorrowedBooks() {
		logger.info("GET: Fetching top 5 most borrowed books");
		List<Object[]> topBooks = borrowRecordServices.findTopBorrowedBooks();
		return ResponseEntity.ok(topBooks);
	}

	@GetMapping("/monthlyCount")
	public ResponseEntity<List<Object[]>> getMonthlyBorrowCounts() {
		logger.info("GET: Fetching monthly borrow counts");
		return ResponseEntity.ok(borrowRecordServices.getMonthlyBorrowCounts());
	}

	@GetMapping("/activeCount")
	public ResponseEntity<Long> countActiveBorrows() {
		return ResponseEntity.ok(borrowRecordServices.countActiveBorrows());
	}

	@PatchMapping("/{id}")
	public BorrowRecord updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
		String status = body.get("status");
		return borrowRecordServices.updateStatus(id, status);
	}

	@GetMapping("/overdue/count")
	public ResponseEntity<Long> countOverdueRecords() {
		logger.info("GET: Counting overdue borrow records");
		return ResponseEntity.ok(borrowRecordServices.countOverdueRecords());
	}

}