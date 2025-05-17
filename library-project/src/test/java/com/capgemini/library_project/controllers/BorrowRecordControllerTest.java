package com.capgemini.library_project.controllers;

import com.capgemini.library_project.entities.BorrowRecord;
import com.capgemini.library_project.services.BorrowRecordServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowRecordControllerTest {

	@Mock
	private BorrowRecordServices borrowRecordServices;

	@InjectMocks
	private BorrowRecordController borrowRecordController;

	@Mock
	private BindingResult bindingResult;

	private BorrowRecord sampleRecord;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		sampleRecord = new BorrowRecord();
		sampleRecord.setBorrowId(1L);
		sampleRecord.setBorrowStatus("Borrowed");
		sampleRecord.setBorrowDate(LocalDate.now());
		sampleRecord.setBorrowReturnDate(LocalDate.now().plusDays(7));
	}

	@Test
	void testCreateBorrowRecord() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(borrowRecordServices.createBorrowRecord(sampleRecord)).thenReturn(sampleRecord);

		ResponseEntity<BorrowRecord> response = borrowRecordController.createBorrowRecord(sampleRecord, bindingResult);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(sampleRecord, response.getBody());
	}

	@Test
	void testGetAllBorrowRecords() {
		when(borrowRecordServices.getAllBorrowRecord()).thenReturn(Arrays.asList(sampleRecord));

		ResponseEntity<List<BorrowRecord>> response = borrowRecordController.getAllBorrowRecords();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
	}

	@Test
	void testGetBorrowRecordById() {
		when(borrowRecordServices.getBorrowRecordById(1L)).thenReturn(sampleRecord);

		ResponseEntity<BorrowRecord> response = borrowRecordController.getBorrowRecordById(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(sampleRecord, response.getBody());
	}

	@Test
	void testGetAllBorrowRecordByUser() {
		when(borrowRecordServices.getAllBorrowRecordByUser(101L)).thenReturn(List.of(sampleRecord));

		ResponseEntity<List<BorrowRecord>> response = borrowRecordController.getAllBorrowRecordByUser(101L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
	}

	@Test
	void testGetAllBorrowRecordByBook() {
		when(borrowRecordServices.getAllBorrowRecordByBook(201L)).thenReturn(List.of(sampleRecord));

		ResponseEntity<List<BorrowRecord>> response = borrowRecordController.getAllBorrowRecordByBook(201L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testGetBorrowRecordsByStatus() {
		when(borrowRecordServices.getBorrowRecordsByStatus("Returned")).thenReturn(List.of(sampleRecord));

		ResponseEntity<List<BorrowRecord>> response = borrowRecordController.getBorrowRecordsByStatus("Returned");

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testGetAllOverdueRecords() {
		when(borrowRecordServices.getAllOverdueRecords()).thenReturn(List.of(sampleRecord));

		ResponseEntity<List<BorrowRecord>> response = borrowRecordController.getAllOverdueRecords();

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testMarkAsReturned() {
		when(borrowRecordServices.markAsReturned(1L)).thenReturn(sampleRecord);

		ResponseEntity<BorrowRecord> response = borrowRecordController.markAsReturned(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(sampleRecord, response.getBody());
	}

	@Test
	void testCalculateFine() {
		when(borrowRecordServices.calculateFine(1L)).thenReturn(50);

		ResponseEntity<Integer> response = borrowRecordController.calculateFine(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(50, response.getBody());
	}

	@Test
	void testCountByStatus() {
		when(borrowRecordServices.countBorrowRecordsByStatus("Borrowed")).thenReturn(3L);

		ResponseEntity<Long> response = borrowRecordController.countByStatus("Borrowed");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(3L, response.getBody());
	}

	@Test
	void testUpdateBorrowRecord() {
		when(bindingResult.hasErrors()).thenReturn(false);
		when(borrowRecordServices.updateBorrowRecord(eq(1L), any(BorrowRecord.class))).thenReturn(sampleRecord);

		ResponseEntity<BorrowRecord> response = borrowRecordController.updateBorrowRecord(1L, sampleRecord,
				bindingResult);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(sampleRecord, response.getBody());
	}

	@Test
	void testDeleteBorrowRecord() {
		doNothing().when(borrowRecordServices).deleteBorrowRecord(1L);

		ResponseEntity<Void> response = borrowRecordController.deleteBorrowRecord(1L);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(borrowRecordServices, times(1)).deleteBorrowRecord(1L);
	}
}
