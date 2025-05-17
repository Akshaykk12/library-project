package com.capgemini.library_project.dto;

public class BookDto {
	private Long bookId;
	private String bookTitle;
	private Long totalCopies;
	private Long availableCopies;
	private String bookCover;
	private String authorName;
	private String categoryName;

	public BookDto(Long bookId, String bookTitle, Long totalCopies, Long availableCopies, String bookCover,
			String authorName, String categoryName) {
		super();
		this.bookId = bookId;
		this.bookTitle = bookTitle;
		this.totalCopies = totalCopies;
		this.availableCopies = availableCopies;
		this.bookCover = bookCover;
		this.authorName = authorName;
		this.categoryName = categoryName;
	}

	public BookDto() {
		super();
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

	public Long getTotalCopies() {
		return totalCopies;
	}

	public void setTotalCopies(Long totalCopies) {
		this.totalCopies = totalCopies;
	}

	public Long getAvailableCopies() {
		return availableCopies;
	}

	public void setAvailableCopies(Long availableCopies) {
		this.availableCopies = availableCopies;
	}

	public String getBookCover() {
		return bookCover;
	}

	public void setBookCover(String bookCover) {
		this.bookCover = bookCover;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

}
