package com.capgemini.library_project.controllers;

import com.capgemini.library_project.controllers.AuthorController;
import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.repositories.AuthorRepository;
import com.capgemini.library_project.services.AuthorServices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthorControllerTest {

    @Mock
    private AuthorServices authorServices;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorController authorController;

    private Author author;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        author = new Author();
        author.setAuthorId(1L);
        author.setAuthorName("Test Author");
    }

    @Test
    void testGetAllAuthors() {
        when(authorServices.findAllAuthors()).thenReturn(Arrays.asList(author));
        ResponseEntity<List<Author>> response = authorController.getAllAuthors();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testCreateAuthor() {
        when(authorServices.createAuthor(author)).thenReturn(author);
        ResponseEntity<Author> response = authorController.createAuthor(author);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Test Author", response.getBody().getAuthorName());
    }

    @Test
    void testUpdateAuthorById() {
        when(authorServices.updateAuthorById(1L, author)).thenReturn(author);
        ResponseEntity<Author> response = authorController.updateAuthorById(1L, author);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testFindAuthorById() {
        when(authorServices.findAuthorById(1L)).thenReturn(author);
        ResponseEntity<Author> response = authorController.findAuthorById(1L);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteAuthorById() {
        when(authorServices.deleteAuthorById(1L)).thenReturn(true);
        ResponseEntity<Boolean> response = authorController.deleteAuthorById(1L);
        assertEquals(204, response.getStatusCodeValue());
        assertTrue(response.getBody());
    }

    @Test
    void testDeleteProfileImage() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        ResponseEntity<Author> response = authorController.deleteProfileImage(1L);
        assertEquals(200, response.getStatusCodeValue());
        verify(authorRepository).save(author);
    }
}
