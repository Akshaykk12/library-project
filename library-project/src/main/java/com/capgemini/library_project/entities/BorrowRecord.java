package com.capgemini.library_project.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class BorrowRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long borrowId;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId")
	@JsonBackReference(value = "user-borrow")
	private User user;

	@ManyToOne
	@JoinColumn(name = "book_id", referencedColumnName = "bookId")
	@JsonBackReference(value = "book-borrow")
	private Book book;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate borrowDate;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate borrowReturnDate;

	private Integer fine;

	private String borrowStatus;

	@JsonProperty("userId")
	public Long getUserId() {
		return user != null ? user.getUserId() : null;
	}

	@JsonProperty("bookId")
	public Long getBookId() {
		return book != null ? book.getBookId() : null;
	}
}