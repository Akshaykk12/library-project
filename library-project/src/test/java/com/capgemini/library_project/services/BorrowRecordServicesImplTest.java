// borrowRecordServicesImplTest.java

package com.capgemini.library_project.services;

import com.capgemini.library_project.dto.BorrowRequest;
import com.capgemini.library_project.entities.Book;
import com.capgemini.library_project.entities.BorrowRecord;
import com.capgemini.library_project.entities.User;
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
        BorrowRequest dto = new BorrowRequest();
        dto.setUserId(user.getUserId());
        dto.setBookId(book.getBookId());

        book.setAvailableCopies(1L);

        when(bookRepository.findById(book.getBookId()))
            .thenReturn(Optional.of(book));
        when(userRepository.findById(user.getUserId()))
            .thenReturn(Optional.of(user));

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
}
