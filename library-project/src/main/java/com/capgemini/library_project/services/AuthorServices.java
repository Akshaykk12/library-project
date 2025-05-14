package com.capgemini.library_project.services;

import java.util.List;

import com.capgemini.library_project.entities.Author;

public interface AuthorServices {
	public Author createAuthor(Author author);
	public Author updateAuthorById(Long id,Author author);
	public Author findAuthorById(Long id);
	public Boolean deleteAuthorById(Long id);
	public List<Author> findAllAuthors();
}
