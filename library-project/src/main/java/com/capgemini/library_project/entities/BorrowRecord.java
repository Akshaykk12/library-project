package com.capgemini.library_project.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	
	private Long userId;
	private Long bookId;
	private LocalDate borrowDate;
	private LocalDate borrowReturnDate;
	private Integer fine;
	
	private String borrowStatus;
}