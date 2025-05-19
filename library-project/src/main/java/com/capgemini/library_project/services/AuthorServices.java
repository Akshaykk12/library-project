package com.capgemini.library_project.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.capgemini.library_project.entities.Author;

public interface AuthorServices {

	public Author updateAuthorById(Long id, Author author);

	public Author findAuthorById(Long id);

	public Boolean deleteAuthorById(Long id);

	public List<Author> findAllAuthors();

	Author updateImage(Long authorId, MultipartFile image) throws IOException;

	public Author getImage(Long authorId);

	Author createAuthor(String authorName, String authorBio, String authorSocial, MultipartFile authorImage)
			throws IOException;
}
