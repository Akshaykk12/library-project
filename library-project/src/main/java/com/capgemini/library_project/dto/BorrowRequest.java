package com.capgemini.library_project.dto;

import java.time.LocalDate;

public class BorrowRequest {
	
	private Long bookId;
    private Long userId;
    private LocalDate issueDate;    
    private LocalDate returnDate;    

    public BorrowRequest() { }

    public BorrowRequest(Long bookId, Long userId,LocalDate issueDate,LocalDate returnDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

    

}
