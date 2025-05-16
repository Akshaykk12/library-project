package com.capgemini.library_project.controllers;

import java.util.List;

import com.capgemini.library_project.entities.BorrowRecord;
import com.capgemini.library_project.services.BorrowRecordServices;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/borrowRecords")
public class BorrowRecordController {

	private static final Logger logger = LoggerFactory.getLogger(BorrowRecordController.class);

	private final BorrowRecordServices borrowRecordServices;

	@Autowired
	public BorrowRecordController(BorrowRecordServices borrowRecordServices) {
		this.borrowRecordServices = borrowRecordServices;
	}

	// issue a book
	@PostMapping
	public ResponseEntity<BorrowRecord> createBorrowRecord(@Valid @RequestBody BorrowRecord borrowRecord,
			BindingResult bindingResult) {
		logger.info("POST: Creating borrow record");
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(borrowRecordServices.createBorrowRecord(borrowRecord));
	}

	// display all issued book records
	@GetMapping
	public ResponseEntity<List<BorrowRecord>> getAllBorrowRecords() {
		logger.info("GET: Fetching all borrow records");
		return ResponseEntity.ok(borrowRecordServices.getAllBorrowRecord());
	}

	// display a single issue by borrowId
	@GetMapping("/{borrowId}")
	public ResponseEntity<BorrowRecord> getBorrowRecordById(@PathVariable Long borrowId) {
		logger.info("GET: Fetching borrow record by ID {}", borrowId);
		return ResponseEntity.ok(borrowRecordServices.getBorrowRecordById(borrowId));
	}

	// display all issue records of a single user by userId
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<BorrowRecord>> getAllBorrowRecordByUser(@PathVariable Long userId) {
		logger.info("GET: Fetching borrow records for user ID {}", userId);
		return ResponseEntity.ok(borrowRecordServices.getAllBorrowRecordByUser(userId));
	}

	// how many times a book was borrowed
	@GetMapping("/book/{bookId}")
	public ResponseEntity<List<BorrowRecord>> getAllBorrowRecordByBook(@PathVariable Long bookId) {
		logger.info("GET: Fetching borrow records for book ID {}", bookId);
		return ResponseEntity.ok(borrowRecordServices.getAllBorrowRecordByBook(bookId));
	}

	// Show all "Returned" or "Overdue" records
	@GetMapping("/status/{status}")
	public ResponseEntity<List<BorrowRecord>> getBorrowRecordsByStatus(@PathVariable String status) {
		logger.info("GET: Fetching records by status '{}'", status);
		return ResponseEntity.ok(borrowRecordServices.getBorrowRecordsByStatus(status));
	}

	// get all borrow records that are overdue
	@GetMapping("/overdue")
	public ResponseEntity<List<BorrowRecord>> getAllOverdueRecords() {
		logger.info("GET: Fetching all overdue borrow records");
		return ResponseEntity.ok(borrowRecordServices.getAllOverdueRecords());
	}

	// quick mark as returned
	@PutMapping("/markReturned/{borrowId}")
	public ResponseEntity<BorrowRecord> markAsReturned(@PathVariable Long borrowId) {
		logger.info("PUT: Marking borrow record {} as returned", borrowId);
		BorrowRecord updatedRecord = borrowRecordServices.markAsReturned(borrowId);
		return ResponseEntity.ok(updatedRecord);
	}

	// fine based on return date
	@GetMapping("/calculateFine/{borrowId}")
	public ResponseEntity<Integer> calculateFine(@PathVariable Long borrowId) {
		logger.info("GET: Calculating fine for borrow ID {}", borrowId);
		return ResponseEntity.ok(borrowRecordServices.calculateFine(borrowId));
	}

	// Count Records by Status (like "Returned", "Borrowed")
	@GetMapping("/countByStatus/{status}")
	public ResponseEntity<Long> countByStatus(@PathVariable String status) {
		logger.info("GET: Counting borrow records with status '{}'", status);
		long count = borrowRecordServices.countBorrowRecordsByStatus(status);
		return ResponseEntity.ok(count);
	}

	// manually update all the details of any issue
	@PutMapping("/{borrowId}")
	public ResponseEntity<BorrowRecord> updateBorrowRecord(@PathVariable Long borrowId,
			@Valid @RequestBody BorrowRecord updatedBorrowRecord, BindingResult bindingResult) {
		logger.info("PUT: Updating borrow record {}", borrowId);
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		return ResponseEntity.ok(borrowRecordServices.updateBorrowRecord(borrowId, updatedBorrowRecord));
	}

	// delete a borrow record
	@DeleteMapping("/{borrowId}")
	public ResponseEntity<Void> deleteBorrowRecord(@PathVariable Long borrowId) {
		logger.info("DELETE: Deleting borrow record with ID {}", borrowId);
		borrowRecordServices.deleteBorrowRecord(borrowId);
		return ResponseEntity.noContent().build();
	}
}
