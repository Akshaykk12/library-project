package com.capgemini.library_project.services;

import com.capgemini.library_project.dto.BorrowRecordDto;
import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.BorrowRecord;
import com.capgemini.library_project.entities.User;
import com.capgemini.library_project.exceptions.AlreadyReturnedException;
import com.capgemini.library_project.exceptions.BookNotFoundException;
import com.capgemini.library_project.exceptions.BorrowRecordNotFoundException;
import com.capgemini.library_project.exceptions.InvalidBorrowDateException;
import com.capgemini.library_project.exceptions.InvalidStatusException;
import com.capgemini.library_project.repositories.BookRepository;
import com.capgemini.library_project.repositories.BorrowRecordRepository;
import com.capgemini.library_project.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowRecordServicesImplTest {

	@Mock
	private BorrowRecordRepository borrowRecordRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BorrowRecordServicesImpl borrowRecordServices;

	private BorrowRecord brecord;
	private User user;
	private Book book;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		user = new User();
		user.setUserId(1L);

		book = new Book();
		book.setBookId(1L);
		book.setAvailableCopies(3L);

		brecord = new BorrowRecord();
		brecord.setBorrowId(1L);
		brecord.setBorrowDate(LocalDate.now().minusDays(10));
		brecord.setBorrowStatus("Borrowed");
		brecord.setUser(user);
		brecord.setBook(book);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(brecord);

		borrowRecordServices.allowedReturnDays = 7;
		borrowRecordServices.finePerDay = 10;
	}

	@Test
	void testCreateBorrowRecord() {
		BorrowRecordDto dto = new BorrowRecordDto();
		dto.setUserId(user.getUserId());
		dto.setBookId(book.getBookId());

		book.setAvailableCopies(1L);

		when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

		BorrowRecord result = borrowRecordServices.borrowBook(dto);

		assertNotNull(result);
		assertEquals("Borrowed", result.getBorrowStatus());
		assertEquals(user, result.getUser());
		assertEquals(book, result.getBook());
		verify(bookRepository).save(book);
	}

	@Test
	void testGetBorrowRecordById() {
		when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(brecord));
		BorrowRecord found = borrowRecordServices.getBorrowRecordById(1L);
		assertEquals(1L, found.getBorrowId());
	}

	@Test
	void testMarkAsReturned_WithFine() {
		when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(brecord));
		when(borrowRecordRepository.save(any())).thenReturn(brecord);

		BorrowRecord result = borrowRecordServices.markAsReturned(1L);
		assertEquals("Returned", result.getBorrowStatus());
		assertTrue(result.getFine() > 0);
	}

	@Test
	void testCalculateFine_WithOverdue() {
		brecord.setBorrowReturnDate(LocalDate.now());
		when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(brecord));

		Integer fine = borrowRecordServices.calculateFine(1L);
		assertEquals(30, fine); // (10 days - 7 allowed) * 10 fine/day = 30
	}

	@Test
	void testDeleteBorrowRecord() {
		when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(brecord));
		borrowRecordServices.deleteBorrowRecord(1L);
		verify(borrowRecordRepository).deleteById(1L);
	}

	@Test
	void testUpdateBorrowRecord() {
		when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(brecord));
		when(borrowRecordRepository.save(any())).thenReturn(brecord);

		BorrowRecord updated = new BorrowRecord();
		updated.setBorrowStatus("Returned");
		updated.setBorrowReturnDate(LocalDate.now());
		updated.setFine(0);
		updated.setUser(user);
		updated.setBook(book);

		BorrowRecord result = borrowRecordServices.updateBorrowRecord(1L, updated);
		assertEquals("Returned", result.getBorrowStatus());
	}

	@Test
	void testGetAllOverdueRecords() {
		brecord.setBorrowReturnDate(LocalDate.now().minusDays(5));
		when(borrowRecordRepository.findAll()).thenReturn(Collections.singletonList(brecord));
		List<BorrowRecord> result = borrowRecordServices.getAllOverdueRecords();
		assertEquals(1, result.size());
	}
	
	@Test
	void testGetAllBorrowRecordByUser() {

	    Long userId = 1L;
	    List<BorrowRecord> records = new ArrayList<>();
	    records.add(brecord);
	    
	    when(borrowRecordRepository.findAllByUser_UserId(userId)).thenReturn(records);

	    List<BorrowRecordDto> result = borrowRecordServices.getAllBorrowRecordByUser(userId);

	    assertNotNull(result);
	    assertEquals(1, result.size());
	    assertEquals(userId, result.get(0).getUserId());
	    verify(borrowRecordRepository).findAllByUser_UserId(userId);
	}

	@Test
	void testCountBorrowRecordsByStatus() {

	    String status = "Borrowed";
	    long expectedCount = 5L;
	    
	    when(borrowRecordRepository.countByBorrowStatus(status)).thenReturn(expectedCount);

	    long result = borrowRecordServices.countBorrowRecordsByStatus(status);

	    assertEquals(expectedCount, result);
	    verify(borrowRecordRepository).countByBorrowStatus(status);
	}

	@Test
	void testCountBorrowRecordsByStatus_InvalidStatus() {

	    String invalidStatus = "InvalidStatus";

	    assertThrows(InvalidStatusException.class, () -> {
	        borrowRecordServices.countBorrowRecordsByStatus(invalidStatus);
	    });
	}

	@Test
	void testGetBorrowRecordsByStatus() {

	    String status = "Borrowed";
	    List<BorrowRecord> records = new ArrayList<>();
	    records.add(brecord);
	    
	    when(borrowRecordRepository.findAllByBorrowStatus(status)).thenReturn(records);

	    List<BorrowRecord> result = borrowRecordServices.getBorrowRecordsByStatus(status);

	    assertNotNull(result);
	    assertEquals(1, result.size());
	    assertEquals(status, result.get(0).getBorrowStatus());
	    verify(borrowRecordRepository).findAllByBorrowStatus(status);
	}

	@Test
	void testGetBorrowRecordsByStatus_InvalidStatus() {

	    String invalidStatus = "InvalidStatus";

	    assertThrows(InvalidStatusException.class, () -> {
	        borrowRecordServices.getBorrowRecordsByStatus(invalidStatus);
	    });
	}

	@Test
	void testUpdateStatus() {

	    Long borrowId = 1L;
	    String newStatus = "Returned";
	    BorrowRecord updatedRecord = new BorrowRecord();
	    updatedRecord.setBorrowId(borrowId);
	    updatedRecord.setBorrowStatus(newStatus);
	    
	    when(borrowRecordRepository.findById(borrowId)).thenReturn(Optional.of(brecord));
	    when(borrowRecordRepository.save(any(BorrowRecord.class))).thenReturn(updatedRecord);

	    BorrowRecord result = borrowRecordServices.updateStatus(borrowId, newStatus);

	    assertNotNull(result);
	    assertEquals(newStatus, result.getBorrowStatus());
	    verify(borrowRecordRepository).findById(borrowId);
	    verify(borrowRecordRepository).save(any(BorrowRecord.class));
	}

	@Test
	void testUpdateStatus_RecordNotFound() {
	    Long nonExistentBorrowId = 999L;
	    String newStatus = "Returned";
	    
	    when(borrowRecordRepository.findById(nonExistentBorrowId)).thenReturn(Optional.empty());
	    assertThrows(BorrowRecordNotFoundException.class, () -> {
	        borrowRecordServices.updateStatus(nonExistentBorrowId, newStatus);
	    });
	}
	
	@Test
	void testUpdateBorrowRecord_InvalidStatus() {
	    when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(brecord));
	    
	    BorrowRecord updated = new BorrowRecord();
	    updated.setBorrowStatus("InvalidStatus");
	    
	    assertThrows(InvalidStatusException.class, () -> {
	        borrowRecordServices.updateBorrowRecord(1L, updated);
	    });
	}

	@Test
	void testUpdateBorrowRecord_InvalidReturnDate() {
	    when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(brecord));
	    
	    BorrowRecord updated = new BorrowRecord();
	    updated.setBorrowReturnDate(LocalDate.now().minusDays(20)); // Before borrow date
	    
	    assertThrows(InvalidBorrowDateException.class, () -> {
	        borrowRecordServices.updateBorrowRecord(1L, updated);
	    });
	}

	@Test
	void testCountOverdueRecords() {
	    brecord.setBorrowReturnDate(LocalDate.now().minusDays(1));
	    when(borrowRecordRepository.findAll()).thenReturn(List.of(brecord));
	    
	    long result = borrowRecordServices.countOverdueRecords();
	    assertEquals(1, result);
	}

	@Test
	void testCountOverdueRecords_NoOverdue() {
	    brecord.setBorrowReturnDate(LocalDate.now().plusDays(1));
	    when(borrowRecordRepository.findAll()).thenReturn(List.of(brecord));
	    
	    long result = borrowRecordServices.countOverdueRecords();
	    assertEquals(0, result);
	}

	@Test
	void testBorrowBook_NoAvailableCopies() {
	    BorrowRecordDto dto = new BorrowRecordDto();
	    dto.setUserId(1L);
	    dto.setBookId(1L);
	    
	    book.setAvailableCopies(0L);
	    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
	    
	    assertThrows(BookNotFoundException.class, () -> {
	        borrowRecordServices.borrowBook(dto);
	    });
	}

	@Test
	void testBorrowBook_InvalidUser() {
	    BorrowRecordDto dto = new BorrowRecordDto();
	    dto.setUserId(999L);
	    dto.setBookId(1L);
	    
	    when(userRepository.findById(999L)).thenReturn(Optional.empty());
	    
	    assertThrows(RuntimeException.class, () -> {
	        borrowRecordServices.borrowBook(dto);
	    });
	}

	@Test
	void testGetBorrowRecordById_NotFound() {
	    when(borrowRecordRepository.findById(999L)).thenReturn(Optional.empty());
	    
	    assertThrows(BorrowRecordNotFoundException.class, () -> {
	        borrowRecordServices.getBorrowRecordById(999L);
	    });
	}

	@Test
	void testGetAllBorrowRecordByBook() {
	    when(borrowRecordRepository.findAllByBook_BookId(1L)).thenReturn(List.of(brecord));
	    
	    List<BorrowRecord> result = borrowRecordServices.getAllBorrowRecordByBook(1L);
	    assertEquals(1, result.size());
	    assertEquals(1L, result.get(0).getBook().getBookId());
	}

	@Test
	void testCalculateFine_NoFine() {
	    brecord.setBorrowDate(LocalDate.now().minusDays(5));
	    brecord.setBorrowReturnDate(LocalDate.now());
	    when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(brecord));
	    
	    Integer fine = borrowRecordServices.calculateFine(1L);
	    assertEquals(0, fine);
	}

	@Test
	void testGetAllBorrowRecord() {
	    when(borrowRecordRepository.findActiveBorrows()).thenReturn(List.of(brecord));
	    
	    List<BorrowRecord> result = borrowRecordServices.getAllBorrowRecord();
	    assertEquals(1, result.size());
	}

	@Test
	void testGetIssuedRecords() {
	    brecord.setBorrowStatus("Borrowed");
	    when(borrowRecordRepository.findByBorrowStatusIn(any())).thenReturn(List.of(brecord));
	    
	    List<BorrowRecord> result = borrowRecordServices.getIssuedRecords();
	    assertEquals(1, result.size());
	    assertEquals("Borrowed", result.get(0).getBorrowStatus());
	}

	@Test
	void testDeleteBorrowRecord_NotFound() {
	    when(borrowRecordRepository.findById(999L)).thenReturn(Optional.empty());
	    
	    assertThrows(BorrowRecordNotFoundException.class, () -> {
	        borrowRecordServices.deleteBorrowRecord(999L);
	    });
	}

	@Test
	void testCountActiveBorrows() {
	    when(borrowRecordRepository.countByBorrowStatus("BORROWED")).thenReturn(5L);
	    
	    long result = borrowRecordServices.countActiveBorrows();
	    assertEquals(5L, result);
	}

	@Test
	void testFindTopBorrowedBooks() {
	    Object[] mockData = new Object[] {1L, "Test Book", 5L};
	    when(borrowRecordRepository.findTopBorrowedBooks()).thenReturn(Collections.singletonList(mockData));
	    
	    List<Object[]> result = borrowRecordServices.findTopBorrowedBooks();
	    assertEquals(1, result.size());
	    assertArrayEquals(mockData, result.get(0));
	}

	@Test
	void testGetMonthlyBorrowCounts() {
	    Object[] mockData = new Object[] {5, 2023, 10L};
	    when(borrowRecordRepository.countBorrowRecordsByMonth()).thenReturn(Collections.singletonList(mockData));
	    
	    List<Object[]> result = borrowRecordServices.getMonthlyBorrowCounts();
	    assertEquals(1, result.size());
	    assertArrayEquals(mockData, result.get(0));
	}

	@Test
	void testMarkAsReturned_AlreadyReturned() {
	    brecord.setBorrowStatus("Returned");
	    when(borrowRecordRepository.findById(1L)).thenReturn(Optional.of(brecord));
	    
	    assertThrows(AlreadyReturnedException.class, () -> {
	        borrowRecordServices.markAsReturned(1L);
	    });
	}
}
