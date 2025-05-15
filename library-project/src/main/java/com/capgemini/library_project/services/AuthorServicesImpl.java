package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.repositories.AuthorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class AuthorServicesImpl implements AuthorServices {

	private static final Logger logger = LoggerFactory.getLogger(AuthorServicesImpl.class);
	private final String UPLOAD_DIR = "uploads/";

	private final AuthorRepository authorRepository;

	@Autowired
	public AuthorServicesImpl(AuthorRepository authorRepository) {
		this.authorRepository = authorRepository;
	}

	@Override
	public Author createAuthor(Author a) {
		logger.info("Creating new author: {}", a.getAuthorName());
		return authorRepository.save(a);
	}

	@Override
	public List<Author> findAllAuthors() {
		logger.info("Fetching all authors");
		return authorRepository.findAll();
	}

	@Override
	public Author updateAuthorById(Long id, Author a) {
		logger.info("Updating author with ID: {}", id);
		Author author = authorRepository.findById(id)
				.orElseThrow(() -> {
					logger.error("Author with ID {} not found", id);
					return new RuntimeException("Author with this id not found " + id);
				});
		author.setAuthorName(a.getAuthorName());
		author.setAuthorBio(a.getAuthorBio());
		author.setAuthorSocial(a.getAuthorSocial());
		return authorRepository.save(author);
	}

	@Override
	public Author findAuthorById(Long id) {
		logger.info("Fetching author with ID: {}", id);
		return authorRepository.findById(id)
				.orElseThrow(() -> {
					logger.error("Author with ID {} not found", id);
					return new RuntimeException("Author with this id not found " + id);
				});
	}

	@Override
	public Boolean deleteAuthorById(Long id) {
		logger.info("Deleting author with ID: {}", id);
		Author author = authorRepository.findById(id)
				.orElseThrow(() -> {
					logger.error("Author with ID {} not found", id);
					return new RuntimeException("Author with this id not found " + id);
				});
		authorRepository.delete(author);
		return true;
	}

	@Override
	public Author updateImage(Long authorId, MultipartFile image) throws IOException {
		logger.info("Updating image for author ID: {}", authorId);
		// Ensure the upload directory exists
		Files.createDirectories(Paths.get(UPLOAD_DIR));

		// Generate a unique file name
		String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
		Path filePath = Paths.get(UPLOAD_DIR, fileName);

		// Save the uploaded image file
		Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		// Find the existing user
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> {
					logger.error("Author with ID {} not found for image update", authorId);
					return new RuntimeException("Author with id " + authorId + " not found");
				});

		// Update the Author image path
		author.setAuthorImage(fileName);

		// Save and return the updated author
		return authorRepository.save(author);
	}

	@Override
	public Author getImage(Long authorid) {
		logger.info("Fetching image for author ID: {}", authorid);
		return authorRepository.findById(authorid).orElseThrow(() -> {
			logger.error("Author with ID {} not found while fetching image", authorid);
			return new RuntimeException();
		});
	}
}
