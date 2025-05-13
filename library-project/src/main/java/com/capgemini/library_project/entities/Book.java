package com.capgemini.library_project.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookId;
	
	@NotNull(message = "Author ID is required")
	@Positive(message= "Author ID cannot be negative")
	private Long authorId;
	
	@NotNull(message = "Category ID is required")
	@Positive(message= "User ID cannot be negative")
	private Long categoryId;
	
	@NotBlank(message = "Book Title is required")
	private String bookTitle;
	
	@NotNull(message = "Total Copies is required")
	@Positive(message= "Total Copies cannot be negative")
	private Integer totalCopies;
	
	@NotNull(message = "Available Copies is required")
	@Positive(message= "Available Copies cannot be negative")
	private String availableCopies;

}