package com.capgemini.library_project.services;

import com.capgemini.library_project.entities.Author;
import com.capgemini.library_project.exceptions.AuthorAlreadyExistsException;
import com.capgemini.library_project.exceptions.AuthorNotFoundException;
import com.capgemini.library_project.repositories.AuthorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class AuthorServicesImpl implements AuthorServices {

	private static final String AUTHOR_NOT_FOUND_MSG = "Author with ID {} not found";

	private static final Logger logger = LoggerFactory.getLogger(AuthorServicesImpl.class);
	private static final String UPLOAD_DIR = "uploads/";

	private final AuthorRepository authorRepository;

	@Autowired
	public AuthorServicesImpl(AuthorRepository authorRepository) {
		this.authorRepository = authorRepository;
	}

	@Override
	public Author createAuthor(String authorName, String authorBio, String authorSocial, MultipartFile authorImage)
			throws IOException {
		Files.createDirectories(Paths.get(UPLOAD_DIR));

		String fileName = UUID.randomUUID() + "_" + authorImage.getOriginalFilename();
		Path filePath = Paths.get(UPLOAD_DIR, fileName);
		Files.copy(authorImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		if (authorRepository.findByAuthorName(authorName).isPresent()) {
			throw new AuthorAlreadyExistsException("Author Already Exists");
		}

		Author author = new Author();
		author.setAuthorBio(authorBio);
		author.setAuthorName(authorName);
		author.setAuthorSocial(authorSocial);
		author.setAuthorImage(fileName);

		logger.info("Creating new author: {}", authorName);
		return authorRepository.save(author);
	}

	@Override
	public List<Author> findAllAuthors() {
		logger.info("Fetching all authors");
		return authorRepository.findAll();
	}

	@Override
	public Author updateAuthorById(Long id, Author a) {
		logger.info("Updating author with ID: {}", id);
		Author author = authorRepository.findById(id).orElseThrow(() -> {
			logger.error(AUTHOR_NOT_FOUND_MSG, id);
			return new AuthorNotFoundException(id);
		});
		author.setAuthorName(a.getAuthorName());
		author.setAuthorBio(a.getAuthorBio());
		author.setAuthorSocial(a.getAuthorSocial());
		return authorRepository.save(author);
	}

	@Override
	public Author findAuthorById(Long id) {
		logger.info("Fetching author with ID: {}", id);
		return authorRepository.findById(id).orElseThrow(() -> {
			logger.error(AUTHOR_NOT_FOUND_MSG, id);
			return new AuthorNotFoundException(id);
		});
	}

	@Override
	public Boolean deleteAuthorById(Long id) {
		logger.info("Deleting author with ID: {}", id);
		Author author = authorRepository.findById(id).orElseThrow(() -> {
			logger.error(AUTHOR_NOT_FOUND_MSG, id);
			return new AuthorNotFoundException(id);
		});
		authorRepository.delete(author);
		return true;
	}

	@Override
	public Author updateImage(Long authorId, MultipartFile image) throws IOException {
		logger.info("Updating image for author ID: {}", authorId);

		Files.createDirectories(Paths.get(UPLOAD_DIR));

		String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
		Path filePath = Paths.get(UPLOAD_DIR, fileName);

		Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		Author author = authorRepository.findById(authorId).orElseThrow(() -> {
			logger.error("Author with ID {} not found for image update", authorId);
			return new AuthorNotFoundException(authorId);
		});

		author.setAuthorImage(fileName);

		return authorRepository.save(author);
	}

	@Override
	public Author getImage(Long authorid) {
		logger.info("Fetching image for author ID: {}", authorid);
		return authorRepository.findById(authorid).orElseThrow(() -> {
			logger.error("Author with ID {} not found while fetching image", authorid);
			return new AuthorNotFoundException(authorid);
		});
	}

}
