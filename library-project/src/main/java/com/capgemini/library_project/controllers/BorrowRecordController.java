package com.capgemini.library_project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.library_project.entities.BorrowRecord;
import com.capgemini.library_project.services.BorrowRecordServices;

@RestController
@RequestMapping("/api/borrowRecords")
public class BorrowRecordController {

	private final BorrowRecordServices borrowRecordServices;

	@Autowired
	public BorrowRecordController(BorrowRecordServices borrowRecordServices) {
		this.borrowRecordServices = borrowRecordServices;
	}

	// issue a book
	@PostMapping
	public ResponseEntity<BorrowRecord> createBorrowRecord(@RequestBody BorrowRecord borrowRecord) {
		return ResponseEntity.status(HttpStatus.CREATED).body(borrowRecordServices.createBorrowRecord(borrowRecord));
	}

	// display all issued book records
	@GetMapping
	public ResponseEntity<List<BorrowRecord>> getAllBorrowRecords() {
		return ResponseEntity.ok(borrowRecordServices.getAllBorrowRecord());
	}

	// display a single issue by borrowId
	@GetMapping("/{borrowId}")
	public ResponseEntity<BorrowRecord> getBorrowRecordById(@PathVariable Long borrowId) {
		return ResponseEntity.ok(borrowRecordServices.getBorrowRecordById(borrowId));
	}

	// display all issue records of a single user by userId
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<BorrowRecord>> getAllBorrowRecordByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(borrowRecordServices.getAllBorrowRecordByUser(userId));
	}

	// how many times a book was borrowed
	@GetMapping("/book/{bookId}")
	public ResponseEntity<List<BorrowRecord>> getAllBorrowRecordByBook(@PathVariable Long bookId) {
		return ResponseEntity.ok(borrowRecordServices.getAllBorrowRecordByBook(bookId));
	}

	// Show all "Returned" or "Overdue" records
	@GetMapping("/status/{status}")
	public ResponseEntity<List<BorrowRecord>> getBorrowRecordsByStatus(@PathVariable String status) {
		return ResponseEntity.ok(borrowRecordServices.getBorrowRecordsByStatus(status));
	}

	// get all borrow records that are overdue
	@GetMapping("/overdue")
	public ResponseEntity<List<BorrowRecord>> getAllOverdueRecords() {
		return ResponseEntity.ok(borrowRecordServices.getAllOverdueRecords());
	}

	// quick mark as returned
	@PutMapping("/markReturned/{borrowId}")
	public ResponseEntity<BorrowRecord> markAsReturned(@PathVariable Long borrowId) {
		BorrowRecord updatedRecord = borrowRecordServices.markAsReturned(borrowId);
		return ResponseEntity.ok(updatedRecord);
	}

	// fine based on return date
	@GetMapping("/calculateFine/{borrowId}")
	public ResponseEntity<Integer> calculateFine(@PathVariable Long borrowId) {
		return ResponseEntity.ok(borrowRecordServices.calculateFine(borrowId));
	}

	// Count Records by Status (like "Returned", "Borrowed")
	@GetMapping("/countByStatus/{status}")
	public ResponseEntity<Long> countByStatus(@PathVariable String status) {
		long count = borrowRecordServices.countBorrowRecordsByStatus(status);
		return ResponseEntity.ok(count);
	}

	// manually update all the details of any issue
	@PutMapping("/{borrowId}")
	public ResponseEntity<BorrowRecord> updateBorrowRecord(@PathVariable Long borrowId,
			@RequestBody BorrowRecord updatedBorrowRecord) {
		return ResponseEntity.ok(borrowRecordServices.updateBorrowRecord(borrowId, updatedBorrowRecord));
	}

	// delete a borrow record
	@DeleteMapping("/{borrowId}")
	public ResponseEntity<Void> deleteBorrowRecord(@PathVariable Long borrowId) {
		borrowRecordServices.deleteBorrowRecord(borrowId);
		return ResponseEntity.noContent().build();
	}

}
