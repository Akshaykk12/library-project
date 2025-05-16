package com.capgemini.library_project.entities;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "authorId")
    @JsonBackReference(value = "author-book")
    private Author author;

    @NotBlank(message = "Book Title is required")
    private String bookTitle;

    @NotNull(message = "Total Copies is required")
    @Positive(message = "Total Copies cannot be negative")
    private Integer totalCopies;

    @NotNull(message = "Available Copies is required")
    @Positive(message = "Available Copies cannot be negative")
    private Integer availableCopies;

    private String bookCover;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id")
    @JsonBackReference(value = "category-book")
    private Category category;

    @OneToMany(mappedBy = "book", cascade = CascadeType.PERSIST)
    @JsonManagedReference(value = "book-review")
    private List<Review> reviews;

    @OneToMany(mappedBy = "book", cascade = CascadeType.PERSIST)
    @JsonManagedReference(value = "book-borrow")
    private List<BorrowRecord> borrowRecords;
}
