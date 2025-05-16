package com.capgemini.library_project.controllers;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.repositories.AuthorRepository;
import com.capgemini.library_project.services.AuthorServices;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/authors")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class AuthorController {

	private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);

	private final AuthorServices authorServices;
	private final AuthorRepository authorRepository;

	@Autowired
	public AuthorController(AuthorServices authorServices, AuthorRepository authorRepository) {
		this.authorServices = authorServices;
		this.authorRepository = authorRepository;
	}

	@GetMapping
	public ResponseEntity<List<Author>> getAllAuthors() {
		logger.info("GET request received: get all authors");
		List<Author> allAuthors = authorServices.findAllAuthors();
		return ResponseEntity.status(HttpStatus.OK).body(allAuthors);
	}

	@PostMapping
	public ResponseEntity<Author> createAuthor(@Valid @RequestBody Author author, BindingResult bindingResult) {
		logger.info("POST request received: create author");
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(authorServices.createAuthor(author));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Author> updateAuthorById(@PathVariable Long id, @Valid @RequestBody Author author,
			BindingResult bindingResult) {
		logger.info("PUT request received: update author with ID {}", id);
		if (bindingResult.hasErrors()) {
			throw new IllegalArgumentException("Invalid Data");
		}
		return ResponseEntity.status(HttpStatus.OK).body(authorServices.updateAuthorById(id, author));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Author> findAuthorById(@PathVariable Long id) {
		logger.info("GET request received: fetch author by ID {}", id);
		return ResponseEntity.status(HttpStatus.OK).body(authorServices.findAuthorById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> deleteAuthorById(@PathVariable Long id) {
		logger.info("DELETE request received: delete author with ID {}", id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(authorServices.deleteAuthorById(id));
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, path = "/profile")
	public ResponseEntity<Author> uploadImage(@RequestParam Long authorId, @RequestParam MultipartFile image)
			throws IOException {
		logger.info("POST request received: upload image for author ID {}", authorId);
		Author saved = authorServices.updateImage(authorId, image);
		return ResponseEntity.status(HttpStatus.OK).body(saved);
	}

	@GetMapping("/images/{image}")
	public ResponseEntity<Resource> getImage(@PathVariable String image) throws IOException {
		logger.info("GET request received: fetch image file {}", image);
		java.nio.file.Path filePath = Paths.get("uploads").resolve(image).normalize();
		Resource resource = new UrlResource(filePath.toUri());

		if (resource.exists() && resource.isReadable()) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
		} else {
			logger.warn("Image {} not found or not readable", image);
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{authorId}/remove-image")
	public ResponseEntity<Author> deleteProfileImage(@PathVariable Long authorId) {
		logger.info("DELETE request received: remove image for author ID {}", authorId);
		Author author = authorRepository.findById(authorId).orElseThrow();
		author.setAuthorImage(null);
		authorRepository.save(author);
		return ResponseEntity.ok().build();
	}
}
