package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.repositories.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServicesImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServicesImpl authorServices;

    private Author author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        author = new Author();
        author.setAuthorId(1L);
        author.setAuthorName("Test Author");
    }

    @Test
    void testCreateAuthor() {
        when(authorRepository.save(author)).thenReturn(author);
        Author saved = authorServices.createAuthor(author);
        assertEquals("Test Author", saved.getAuthorName());
    }

    @Test
    void testFindAllAuthors() {
        when(authorRepository.findAll()).thenReturn(Collections.singletonList(author));
        List<Author> authors = authorServices.findAllAuthors();
        assertEquals(1, authors.size());
    }

    @Test
    void testUpdateAuthorById() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        Author updated = new Author();
        updated.setAuthorName("Updated Name");
        updated.setAuthorBio("Updated Bio");
        updated.setAuthorSocial("Social");

        Author result = authorServices.updateAuthorById(1L, updated);
        assertEquals("Updated Name", result.getAuthorName());
    }

    @Test
    void testFindAuthorById() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        Author found = authorServices.findAuthorById(1L);
        assertEquals("Test Author", found.getAuthorName());
    }

    @Test
    void testDeleteAuthorById() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        Boolean result = authorServices.deleteAuthorById(1L);
        assertTrue(result);
        verify(authorRepository).delete(author);
    }

    @Test
    void testUpdateImage() throws IOException {
        MockMultipartFile image = new MockMultipartFile("file", "image.jpg", "image/jpeg", "image-data".getBytes());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        Author updated = authorServices.updateImage(1L, image);
        assertNotNull(updated);
        verify(authorRepository).save(author);
    }

    @Test
    void testGetImage() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        Author result = authorServices.getImage(1L);
        assertEquals("Test Author", result.getAuthorName());
    }
}
