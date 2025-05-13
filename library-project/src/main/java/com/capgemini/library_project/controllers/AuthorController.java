package com.capgemini.library_project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.services.AuthorServices;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/authors")
public class AuthorController {
	private AuthorServices authorServices;
	
	@Autowired
	public AuthorController(AuthorServices authorServices) {
		// TODO Auto-generated constructor stub
		this.authorServices =authorServices;
	}
	
	@GetMapping
	public ResponseEntity<List<Author>> getAllAuthors()
	{
		List<Author> allAuthors = authorServices.findAllAuthors();
		return ResponseEntity.status(HttpStatus.OK).body(allAuthors);
	}
	
	@PostMapping
	public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
		return ResponseEntity.status(201).body(authorServices.createAuthor(author));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Author> updateAuthorById(@PathVariable Long id,@RequestBody Author author) {
		return ResponseEntity.status(200).body(authorServices.updateAuthorById(id, author));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Author> findAuthorById(@PathVariable Long id) {
		return ResponseEntity.status(200).body(authorServices.findAuthorById(id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> deleteAuthorById(@PathVariable Long id) {
		
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(authorServices.deleteAuthorById(id));
	}
}
