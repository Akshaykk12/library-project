package com.capgemini.library_project.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.capgemini.library_project.entities.BorrowRecord;
import com.capgemini.library_project.repositories.BorrowRecordRepository;

@Service
public class BorrowRecordServicesImpl implements BorrowRecordServices {

	private final BorrowRecordRepository borrowRecordRepository;

	@Value("${borrow.return.days}")
	public int allowedReturnDays;

	@Value("${borrow.fine.per.day}")
	public int finePerDay;

	@Autowired
	public BorrowRecordServicesImpl(BorrowRecordRepository borrowRecordRepository) {
		this.borrowRecordRepository = borrowRecordRepository;
	}

	@Override
	public BorrowRecord createBorrowRecord(BorrowRecord borrowRecord) {
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
		return borrowRecordRepository.findAllByUserId(userId);
	}

	// how many times a book was borrowed
	@Override
	public List<BorrowRecord> getAllBorrowRecordByBook(Long bookId) {
		return borrowRecordRepository.findAllByBookId(bookId);
	}

	// Show all "Returned" or "Overdue" records
	public List<BorrowRecord> getBorrowRecordsByStatus(String status) {
		return borrowRecordRepository.findAllByBorrowStatus(status);
	}

//	// get all borrow records that are overdue
	@Override
	public List<BorrowRecord> getAllOverdueRecords() {
		LocalDate today = LocalDate.now();
		return borrowRecordRepository.findAll().stream().filter(
				record -> record.getBorrowReturnDate().isBefore(today) && record.getBorrowStatus().equals("Borrowed"))
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

//	// fine based on return date
	@Override
	public Integer calculateFine(Long borrowId) {
		BorrowRecord record = borrowRecordRepository.findById(borrowId)
				.orElseThrow(() -> new RuntimeException("Record not found"));
		// fine is Rs.5 per day overdue
		long daysOverdue = ChronoUnit.DAYS.between(record.getBorrowReturnDate(), LocalDate.now());
		return daysOverdue > 0 ? (int) (daysOverdue * 5) : 0;
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
