package com.capgemini.library_project.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;

    @NotBlank(message = "Author Name is required")
    private String authorName;
    
    @NotBlank(message = "Author Bio is required")
    private String authorBio;
    @NotBlank(message = "Author Social is required")
    private String authorSocial;
    private String authorImage;

    @OneToMany(mappedBy = "author", cascade = CascadeType.PERSIST)
    @JsonManagedReference(value = "author-book")
    private List<Book> books;
}
