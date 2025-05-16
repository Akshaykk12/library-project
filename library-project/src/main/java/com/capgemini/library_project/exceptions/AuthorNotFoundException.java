package com.capgemini.library_project.exceptions;

public class AuthorNotFoundException extends RuntimeException {

	public AuthorNotFoundException(Long authorid) {

		super("Author with id " + authorid + " not found");
	}
	
	public AuthorNotFoundException(String authorid) {

		super("Author with id " + authorid + " not found");
	}
}
