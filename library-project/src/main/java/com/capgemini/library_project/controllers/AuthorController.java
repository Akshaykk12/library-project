package com.capgemini.library_project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;



import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.repositories.AuthorRepository;
import com.capgemini.library_project.services.AuthorServices;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/authors")
public class AuthorController {
	private AuthorServices authorServices;
	private AuthorRepository authorRepository;
	
	@Autowired
	public AuthorController(AuthorServices authorServices, AuthorRepository authorRepository) {
		// TODO Auto-generated constructor stub
		this.authorServices =authorServices;
		this.authorRepository = authorRepository;
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
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/profile")
	public ResponseEntity<Author> uploadImage(@RequestParam Long authorId, @RequestParam MultipartFile image) throws IOException{
		Author saved = authorServices.updateImage(authorId, image);
		return ResponseEntity.status(HttpStatus.OK).body(saved);
	}
	
	@GetMapping("/images/{image}")
	public ResponseEntity<Resource> getImage(@PathVariable String image) throws IOException {
	    java.nio.file.Path filePath = Paths.get("uploads").resolve(image).normalize();
	    Resource resource = new UrlResource(filePath.toUri());

	    if (resource.exists() && resource.isReadable()) {
	        return ResponseEntity.ok()
	            .contentType(MediaType.IMAGE_JPEG)
	            .body(resource);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@DeleteMapping("/{authorId}/remove-image")
	public ResponseEntity<Author> deleteProfileImage(@PathVariable Long authorId) {
	    Author author = authorRepository.findById(authorId).orElseThrow();
	    author.setAuthorImage(null);
	    authorRepository.save(author);
	    return ResponseEntity.ok().build();  
	}
}
