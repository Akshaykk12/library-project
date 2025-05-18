package com.capgemini.library_project.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class,
		  property = "bookId"
		)

public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "authorId")
    private Author author;

    @NotBlank(message = "Book Title is required")
    private String bookTitle;

    @NotNull(message = "Total Copies is required")
    @Positive(message = "Total Copies cannot be negative")
    private Long totalCopies;

    @NotNull(message = "Available Copies is required")
    @Positive(message = "Available Copies cannot be negative")
    private Long availableCopies;

    private String bookCover;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "book", cascade = CascadeType.PERSIST)
    @JsonManagedReference(value = "book-review")
    private List<Review> reviews;

    @OneToMany(mappedBy = "book", cascade = CascadeType.PERSIST)
    @JsonManagedReference(value = "book-borrow")
    private List<BorrowRecord> borrowRecords;
}
