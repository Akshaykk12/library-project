package com.capgemini.library_project.dto;

import java.time.LocalDate;

public class BorrowRecordDto {
	
	private Long borrowId;
    private Long userId;
    private Long bookId;
    private String bookTitle;  // add this
    private LocalDate borrowDate;
    private LocalDate borrowReturnDate;
    private String borrowStatus;
    
    
	public BorrowRecordDto() {
		super();
	}


	public BorrowRecordDto(Long borrowId, Long userId, Long bookId, String bookTitle,
			LocalDate borrowDate, LocalDate borrowReturnDate, String borrowStatus) {
		super();
		this.borrowId = borrowId;
		this.userId = userId;
		this.bookId = bookId;
		this.bookTitle = bookTitle;
		this.borrowDate = borrowDate;
		this.borrowReturnDate = borrowReturnDate;
		this.borrowStatus = borrowStatus;
	}


	public Long getBorrowId() {
		return borrowId;
	}


	public void setBorrowId(Long borrowId) {
		this.borrowId = borrowId;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getBookId() {
		return bookId;
	}


	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}


	public String getBookTitle() {
		return bookTitle;
	}


	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}


	public LocalDate getBorrowDate() {
		return borrowDate;
	}


	public void setBorrowDate(LocalDate borrowDate) {
		this.borrowDate = borrowDate;
	}


	public LocalDate getBorrowReturnDate() {
		return borrowReturnDate;
	}


	public void setBorrowReturnDate(LocalDate borrowReturnDate) {
		this.borrowReturnDate = borrowReturnDate;
	}


	public String getBorrowStatus() {
		return borrowStatus;
	}


	public void setBorrowStatus(String borrowStatus) {
		this.borrowStatus = borrowStatus;
	}
    

}
