package com.capgemini.library_project.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class BorrowRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long borrowId;
	
	@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
//	@JsonBackReference
    private User user;
	
	@ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "bookId")
//	@JsonBackReference
    private Book book;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate borrowDate;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate borrowReturnDate;
	private Integer fine;
	
	private String borrowStatus;
}